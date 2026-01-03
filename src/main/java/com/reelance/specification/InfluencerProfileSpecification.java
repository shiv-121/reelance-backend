package com.reelance.specification;

import com.reelance.entity.InfluencerProfile;
import org.springframework.data.jpa.domain.Specification;

public class InfluencerProfileSpecification {

    public static Specification<InfluencerProfile> hasNiche(String niche) {
        return (root, query, cb) ->
                niche == null ? null :
                        cb.equal(
                                cb.lower(root.get("niche")),
                                niche.toLowerCase()
                        );
    }

    public static Specification<InfluencerProfile> minFollowers(Long min) {
        return (root, query, cb) ->
                min == null ? null :
                        cb.greaterThanOrEqualTo(root.get("followers"), min);
    }

    public static Specification<InfluencerProfile> maxFollowers(Long max) {
        return (root, query, cb) ->
                max == null ? null :
                        cb.lessThanOrEqualTo(root.get("followers"), max);
    }

    public static Specification<InfluencerProfile> minPrice(Long min) {
        return (root, query, cb) ->
                min == null ? null :
                        cb.greaterThanOrEqualTo(root.get("pricePerPost"), min);
    }

    public static Specification<InfluencerProfile> maxPrice(Long max) {
        return (root, query, cb) ->
                max == null ? null :
                        cb.lessThanOrEqualTo(root.get("pricePerPost"), max);
    }
}
