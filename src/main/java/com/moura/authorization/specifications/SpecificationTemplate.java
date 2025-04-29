package com.moura.authorization.specifications;

import com.moura.authorization.context.TenantContext;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    private final static String TENANT_COLUMN_NAME = "organizationId";

    public static <T> Specification<T> tenantId() {
        return (root, query, cb) -> {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) throw new IllegalStateException("TenantId is not set");
            return cb.equal(root.get(TENANT_COLUMN_NAME), tenantId);
        };
    }

    public static <T> Specification<T> tenantAndIdIn(String field, Collection<?> ids) {
        return (root, query, cb) -> {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                throw new IllegalStateException("TenantId is not set");
            }

            return cb.and(
                    cb.equal(root.get(TENANT_COLUMN_NAME), tenantId),
                    root.get(field).in(ids)
            );
        };
    }
}
