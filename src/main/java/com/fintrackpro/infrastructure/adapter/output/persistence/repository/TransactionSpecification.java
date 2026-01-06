package com.fintrackpro.infrastructure.adapter.output.persistence.repository;

import com.fintrackpro.domain.model.TransactionFilter;
import com.fintrackpro.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification builder for dynamic Transaction filtering.
 */
public class TransactionSpecification {

    public static Specification<TransactionEntity> withFilter(Long userId, TransactionFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter by user and non-deleted
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            predicates.add(cb.equal(root.get("deleted"), false));

            if (filter.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), filter.startDate()));
            }

            if (filter.endDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), filter.endDate()));
            }

            if (filter.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            }

            if (filter.walletId() != null) {
                predicates.add(cb.equal(root.get("wallet").get("id"), filter.walletId()));
            }

            if (filter.type() != null) {
                predicates.add(cb.equal(root.get("type"), filter.type()));
            }

            if (filter.minAmount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), filter.minAmount()));
            }

            if (filter.maxAmount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), filter.maxAmount()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
