package com.reelance.repository;

import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {

    List<Campaign> findByBrand_Id(Long brandId);

    List<Campaign> findByStatus(CampaignStatus status);

    long countByBrand_Id(Long brandId);

    long countByBrand_IdAndStatus(
            Long brandId,
            CampaignStatus status);

    List<Campaign> findTop5ByBrand_IdOrderByCreatedAtDesc(Long brandId);

    @Query("SELECT COALESCE(SUM(c.budget), 0) FROM Campaign c WHERE c.brand.id = :brandId")
    long sumBudgetByBrandId(Long brandId);
}

