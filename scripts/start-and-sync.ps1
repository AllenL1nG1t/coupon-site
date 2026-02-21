param(
    [string]$HealthUrl = "http://localhost:8081/",
    [int]$HealthTimeoutSec = 90,
    [string]$Branch = "main",
    [string]$CommitMessage = ""
)

$ErrorActionPreference = "Stop"

function Get-MavenPath {
    $candidates = @(
        "mvn",
        "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2023.2\plugins\maven\lib\maven3\bin\mvn.cmd"
    )
    foreach ($c in $candidates) {
        try {
            if ($c -eq "mvn") {
                & $c -v *> $null
                return $c
            }
            if (Test-Path $c) {
                return $c
            }
        } catch {
            continue
        }
    }
    throw "Maven not found."
}

function Wait-Health([string]$Url, [int]$TimeoutSec) {
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        try {
            $res = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 4
            if ($res.StatusCode -ge 200 -and $res.StatusCode -lt 500) {
                return $true
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    return $false
}

Write-Host "[1/4] Starting Spring Boot..."
$mvn = Get-MavenPath
$proc = Start-Process -FilePath $mvn -ArgumentList "spring-boot:run" -WorkingDirectory (Get-Location) -PassThru
Write-Host "Started process id: $($proc.Id)"

Write-Host "[2/4] Waiting for health check: $HealthUrl"
if (-not (Wait-Health -Url $HealthUrl -TimeoutSec $HealthTimeoutSec)) {
    throw "Service did not become healthy within $HealthTimeoutSec seconds."
}
Write-Host "Health check passed."

Write-Host "[3/4] Syncing code to GitHub..."
$status = git status --porcelain
if (-not $status) {
    Write-Host "No local changes. Skip commit/push."
    exit 0
}

git add -A
if ([string]::IsNullOrWhiteSpace($CommitMessage)) {
    $ts = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $CommitMessage = "chore: sync after successful startup ($ts)"
}
git commit -m $CommitMessage
git push origin $Branch

Write-Host "[4/4] Done. Latest code pushed to GitHub."
