package com.reelance.repository;

import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByBrand_Id(Long brandId);

    List<Campaign> findByStatus(CampaignStatus status);
}

