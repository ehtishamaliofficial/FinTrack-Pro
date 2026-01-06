package com.fintrackpro.infrastructure.helper;

import com.fintrackpro.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.WalletEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Helper component for creating JPA entity references without loading them from database.
 * Uses JPA's getReference() method which creates a proxy object for performance optimization.
 *
 * This is part of the infrastructure layer and handles persistence-specific concerns.
 * It's NOT a domain service - it's an infrastructure utility for mappers and adapters.
 *
 * @author FinTrack Pro Team
 */
@Component
@RequiredArgsConstructor
public class EntityReferenceHelper {

    private final EntityManager entityManager;

    /**
     * Creates a UserEntity reference without loading from database.
     * Returns a lazy-loaded proxy that will only hit the database if accessed.
     *
     * Performance Note: This method does NOT query the database. It creates a proxy
     * that will only be initialized when you access its properties.
     *
     * @param userId the user ID
     * @return UserEntity reference or null if userId is null
     * @throws jakarta.persistence.EntityNotFoundException if the entity doesn't exist when accessed
     */
    public UserEntity getUserReference(Long userId) {
        if (userId == null) {
            return null;
        }
        return entityManager.getReference(UserEntity.class, userId);
    }

    /**
     * Creates a WalletEntity reference without loading from database.
     *
     * @param walletId the wallet ID
     * @return WalletEntity reference or null if walletId is null
     * @throws jakarta.persistence.EntityNotFoundException if the entity doesn't exist when accessed
     */
    public WalletEntity getWalletReference(Long walletId) {
        if (walletId == null) {
            return null;
        }
        return entityManager.getReference(WalletEntity.class, walletId);
    }



    /**
     * Safely extracts the ID from an entity, handling null cases.
     * Useful when converting entities back to domain models.
     *
     * @param entity the entity to extract ID from
     * @param <ID> ID type (typically Long)
     * @return the entity ID or null
     */
    @SuppressWarnings("unchecked")
    public <ID> ID extractId(Object entity) {
        if (entity == null) {
            return null;
        }

        try {
            var method = entity.getClass().getMethod("getId");
            return (ID) method.invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely extracts Long ID from an entity.
     * Type-safe variant of extractId() specifically for Long IDs.
     *
     * @param entity the entity to extract ID from
     * @return the entity ID or null
     */
    public Long extractLongId(Object entity) {
        return extractId(entity);
    }
}
