package com.reelance.specification;

import com.reelance.entity.Campaign;
import com.reelance.entity.CampaignStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CampaignSpecification {

    public static Specification<Campaign> filterByBrandAndCriteria(
            Long brandId,
            String search,
            CampaignStatus status,
            String niche,
            Long budgetMin,
            Long budgetMax,
            LocalDate deadlineBefore,
            LocalDate deadlineAfter) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Brand filter - mandatory
            predicates.add(cb.equal(root.get("brand").get("id"), brandId));

            // Search filter - case-insensitive partial match on title and description
            if (search != null && !search.isBlank()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
                Predicate descriptionMatch = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(titleMatch, descriptionMatch));
            }

            // Status filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Niche filter
            if (niche != null && !niche.isBlank()) {
                predicates.add(cb.equal(root.get("niche"), niche));
            }

            // Budget range filter
            if (budgetMin != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("budget"), budgetMin));
            }
            if (budgetMax != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("budget"), budgetMax));
            }

            // Deadline range filter
            if (deadlineBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("deadline"), deadlineBefore));
            }
            if (deadlineAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("deadline"), deadlineAfter));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

