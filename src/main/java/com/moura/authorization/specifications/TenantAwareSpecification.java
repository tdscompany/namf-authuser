package com.moura.authorization.specifications;

import com.moura.authorization.context.TenantContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TenantAwareSpecification<T> implements Specification<T> {

    private final String TENANT_COLUMN_NAME = "organization_id";

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("TenantId is not set");
        }

        if (!root.getModel().getAttributes().stream().anyMatch(a -> a.getName().equals(TENANT_COLUMN_NAME))) {
            throw new IllegalStateException("Entity does not have tenantId field");
        }

        return cb.equal(root.get(TENANT_COLUMN_NAME), tenantId);
    }
}