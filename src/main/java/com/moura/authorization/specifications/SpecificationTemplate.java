package com.moura.authorization.specifications;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.utils.MessageUtils;
import jakarta.persistence.criteria.Join;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    private final static String TENANT_COLUMN_NAME = "organizationId";

    @And({
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "name", spec = Like.class),
            @Spec(path = "email", spec = Like.class),
    })
    public interface UserSpec extends Specification<User> {}

    public static Specification<User> tenantFilter() {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) throw new IllegalStateException(MessageUtils.get("error.tenant_not_found"));
        return (root, query, cb) -> cb.equal(root.get(TENANT_COLUMN_NAME), tenantId);
    }

    public static <T> Specification<T> tenantAndIdIn(String field, Collection<?> ids) {
        return (root, query, cb) -> {
            UUID tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null) {
                throw new IllegalStateException(MessageUtils.get("error.tenant_not_found"));
            }

            return cb.and(
                    cb.equal(root.get(TENANT_COLUMN_NAME), tenantId),
                    root.get(field).in(ids)
            );
        };
    }

    public static Specification<User> userGroupId(UUID groupId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<User, Group> groupJoin = root.join("groups");
            return cb.equal(groupJoin.get("id"), groupId);
        };
    }
}
