package com.couponsite.admin;

import com.couponsite.brand.BrandProfileRepository;
import com.couponsite.coupon.StagedCouponRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CrawlerReportService {

    private final CrawlerSiteService crawlerSiteService;
    private final StagedCouponRepository stagedCouponRepository;
    private final BrandProfileRepository brandProfileRepository;
    private final StagedBrandLogoRepository stagedBrandLogoRepository;

    public CrawlerReportService(
        CrawlerSiteService crawlerSiteService,
        StagedCouponRepository stagedCouponRepository,
        BrandProfileRepository brandProfileRepository,
        StagedBrandLogoRepository stagedBrandLogoRepository
    ) {
        this.crawlerSiteService = crawlerSiteService;
        this.stagedCouponRepository = stagedCouponRepository;
        this.brandProfileRepository = brandProfileRepository;
        this.stagedBrandLogoRepository = stagedBrandLogoRepository;
    }

    public CrawlerReportDto build() {
        Map<String, Long> couponBySource = stagedCouponRepository.findAllByOrderByUpdatedAtDesc().stream()
            .collect(Collectors.groupingBy(c -> normalize(c.getSource()), Collectors.counting()));

        Map<String, Long> logoBySource = stagedBrandLogoRepository.findAllByOrderByUpdatedAtDesc().stream()
            .collect(Collectors.groupingBy(c -> normalize(c.getSourceSiteKey()), Collectors.counting()));

        List<CrawlerSiteReportDto> sites = new ArrayList<>();
        for (CrawlerSite site : crawlerSiteService.listEntities()) {
            String key = normalize(site.getSiteKey());
            long couponCount = couponBySource.entrySet().stream()
                .filter(entry -> entry.getKey().contains(key))
                .mapToLong(Map.Entry::getValue)
                .sum();
            long logoCount = logoBySource.getOrDefault(key, 0L);
            long brandCount = brandProfileRepository.findAllByOrderByStoreNameAsc().stream()
                .filter(brand -> normalize(brand.getOfficialUrl()).contains(normalize(site.getBaseUrl())))
                .count();
            sites.add(new CrawlerSiteReportDto(site.getSiteKey(), site.getSiteName(), couponCount, brandCount, logoCount));
        }

        return new CrawlerReportDto(
            stagedCouponRepository.count(),
            brandProfileRepository.count(),
            stagedBrandLogoRepository.count(),
            sites
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
