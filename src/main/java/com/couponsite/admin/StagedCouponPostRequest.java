package com.couponsite.admin;

import java.util.List;

public record StagedCouponPostRequest(List<Long> ids) {
}
