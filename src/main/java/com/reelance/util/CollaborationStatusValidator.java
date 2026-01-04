package com.reelance.util;

import com.reelance.entity.CollaborationStatus;

import java.util.Set;

public class CollaborationStatusValidator {

    public static boolean isValidTransition(
            CollaborationStatus current,
            CollaborationStatus next
    ) {
        return switch (current) {
            case PENDING ->
                    Set.of(
                            CollaborationStatus.ACCEPTED,
                            CollaborationStatus.REJECTED,
                            CollaborationStatus.CANCELLED
                    ).contains(next);

            case ACCEPTED ->
                    Set.of(
                            CollaborationStatus.DELIVERED,
                            CollaborationStatus.CANCELLED
                    ).contains(next);

            case DELIVERED ->
                    next == CollaborationStatus.COMPLETED;

            default -> false;
        };
    }
}
