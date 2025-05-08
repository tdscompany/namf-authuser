package com.moura.authorization.groups.repositories.specification;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.groups.dtos.GroupFilterDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.entities.Group_;
import com.moura.authorization.utils.MessageUtils;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupSpecification  implements Specification<Group> {

    private final GroupFilterDTO filter;

    public GroupSpecification(GroupFilterDTO filter) {
        this.filter = filter;
    }

    public static Specification<Group> of(GroupFilterDTO filter) {
        return new GroupSpecification(filter);
    }
    @Override
    public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException(MessageUtils.get("error.tenant_not_found"));
        }

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Group_.ORGANIZATION_ID), tenantId));

        addLikePredicate(cb, predicates, root.get(Group_.NAME), filter.getName());

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void addLikePredicate(CriteriaBuilder cb, List<Predicate> predicates, Path<String> path, String value) {
        if (value != null) {
            predicates.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    private void addEqualPredicate(CriteriaBuilder cb, List<Predicate> predicates, Path<?> path, Object value) {
        if (value != null) {
            predicates.add(cb.equal(path, value));
        }
    }
}
