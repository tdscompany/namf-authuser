package com.moura.authorization.users.repositories.specification;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.users.dtos.UserFilterDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.entities.User_;
import com.moura.authorization.utils.MessageUtils;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserSpecification implements Specification<User> {

    private final UserFilterDTO filter;

    public UserSpecification(UserFilterDTO filter) {
        this.filter = filter;
    }

    public static Specification<User> of(UserFilterDTO filter) {
        return new UserSpecification(filter);
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException(MessageUtils.get("error.tenant_not_found"));
        }

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(User_.ORGANIZATION_ID), tenantId));

        addLikePredicate(cb, predicates, root.get(User_.NAME), filter.getName());
        addLikePredicate(cb, predicates, root.get(User_.EMAIL), filter.getEmail());
        addEqualPredicate(cb, predicates, root.get(User_.USER_STATUS), filter.getUserStatus());

        if (filter.getGroupId() != null) {
            addGroupPredicate(query, cb, predicates, root);
        }

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

    private void addGroupPredicate(CriteriaQuery<?> query, CriteriaBuilder cb, List<Predicate> predicates, Root<User> root) {
        Join<User, Group> groupJoin = root.join(User_.GROUPS, JoinType.INNER);
        predicates.add(cb.equal(groupJoin.get("id"), filter.getGroupId()));
        query.distinct(true);
    }
}

