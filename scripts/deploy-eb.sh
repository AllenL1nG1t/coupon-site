#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
One-click deploy to Elastic Beanstalk (Java 17 / Spring Boot).

Usage:
  ./scripts/deploy-eb.sh [-f path/to/deploy.env]

Required env vars:
  AWS_REGION
  EB_APP_NAME
  EB_ENV_NAME
  DB_HOST
  DB_PORT
  DB_NAME
  DB_USERNAME
  DB_PASSWORD

Optional env vars:
  EB_PLATFORM            default: Corretto 17 running on 64bit Amazon Linux 2023
  EB_INSTANCE_TYPE       default: t3.small
  EB_SINGLE_INSTANCE     default: true (set false for load-balanced)
  EB_CNAME               default: empty
  EB_PROFILE             default: empty (use aws default profile if empty)
  SKIP_TESTS             default: true

Example:
  ./scripts/deploy-eb.sh -f ./scripts/deploy-eb.env.example
EOF
}

ENV_FILE=""
while getopts ":f:h" opt; do
  case "${opt}" in
    f) ENV_FILE="${OPTARG}" ;;
    h) usage; exit 0 ;;
    *) usage; exit 1 ;;
  esac
done

if [[ -n "${ENV_FILE}" ]]; then
  if [[ ! -f "${ENV_FILE}" ]]; then
    echo "ERROR: env file not found: ${ENV_FILE}" >&2
    exit 1
  fi
  set -a
  # shellcheck source=/dev/null
  source "${ENV_FILE}"
  set +a
fi

for cmd in aws eb mvn git; do
  if ! command -v "${cmd}" >/dev/null 2>&1; then
    echo "ERROR: '${cmd}' not found in PATH." >&2
    exit 1
  fi
done

require_var() {
  local k="$1"
  if [[ -z "${!k:-}" ]]; then
    echo "ERROR: required env var missing: ${k}" >&2
    exit 1
  fi
}

require_var AWS_REGION
require_var EB_APP_NAME
require_var EB_ENV_NAME
require_var DB_HOST
require_var DB_PORT
require_var DB_NAME
require_var DB_USERNAME
require_var DB_PASSWORD

EB_PLATFORM="${EB_PLATFORM:-Corretto 17 running on 64bit Amazon Linux 2023}"
EB_INSTANCE_TYPE="${EB_INSTANCE_TYPE:-t3.small}"
EB_SINGLE_INSTANCE="${EB_SINGLE_INSTANCE:-true}"
EB_CNAME="${EB_CNAME:-}"
EB_PROFILE="${EB_PROFILE:-}"
SKIP_TESTS="${SKIP_TESTS:-true}"

AWS_ARGS=()
if [[ -n "${EB_PROFILE}" ]]; then
  AWS_ARGS+=(--profile "${EB_PROFILE}")
fi

if ! aws "${AWS_ARGS[@]}" sts get-caller-identity >/dev/null 2>&1; then
  echo "ERROR: AWS credentials are not configured or invalid." >&2
  exit 1
fi

echo "[1/6] Building jar..."
if [[ "${SKIP_TESTS}" == "true" ]]; then
  mvn -DskipTests clean package
else
  mvn clean package
fi

echo "[2/6] Initializing Elastic Beanstalk app..."
EB_INIT_CMD=(eb init "${EB_APP_NAME}" --platform "${EB_PLATFORM}" --region "${AWS_REGION}")
if [[ -n "${EB_PROFILE}" ]]; then
  EB_INIT_CMD+=(--profile "${EB_PROFILE}")
fi
"${EB_INIT_CMD[@]}"

echo "[3/6] Ensuring EB environment exists..."
EB_USE_CMD=(eb use "${EB_ENV_NAME}")
if [[ -n "${EB_PROFILE}" ]]; then
  EB_USE_CMD+=(--profile "${EB_PROFILE}")
fi
if ! "${EB_USE_CMD[@]}" >/dev/null 2>&1; then
  EB_CREATE_CMD=(eb create "${EB_ENV_NAME}" --instance_type "${EB_INSTANCE_TYPE}")
  if [[ "${EB_SINGLE_INSTANCE}" == "true" ]]; then
    EB_CREATE_CMD+=(--single)
  fi
  if [[ -n "${EB_CNAME}" ]]; then
    EB_CREATE_CMD+=(--cname "${EB_CNAME}")
  fi
  if [[ -n "${EB_PROFILE}" ]]; then
    EB_CREATE_CMD+=(--profile "${EB_PROFILE}")
  fi
  "${EB_CREATE_CMD[@]}"
else
  "${EB_USE_CMD[@]}"
fi

echo "[4/6] Setting runtime environment variables..."
ENVVARS="DB_HOST=${DB_HOST},DB_PORT=${DB_PORT},DB_NAME=${DB_NAME},DB_USERNAME=${DB_USERNAME},DB_PASSWORD=${DB_PASSWORD}"
EB_SETENV_CMD=(eb setenv "${ENVVARS}")
if [[ -n "${EB_PROFILE}" ]]; then
  EB_SETENV_CMD+=(--profile "${EB_PROFILE}")
fi
"${EB_SETENV_CMD[@]}"

echo "[5/6] Deploying to EB..."
EB_DEPLOY_CMD=(eb deploy "${EB_ENV_NAME}" --staged)
if [[ -n "${EB_PROFILE}" ]]; then
  EB_DEPLOY_CMD+=(--profile "${EB_PROFILE}")
fi
"${EB_DEPLOY_CMD[@]}"

echo "[6/6] Deployment status:"
EB_STATUS_CMD=(eb status "${EB_ENV_NAME}")
if [[ -n "${EB_PROFILE}" ]]; then
  EB_STATUS_CMD+=(--profile "${EB_PROFILE}")
fi
"${EB_STATUS_CMD[@]}"

echo "Done."
