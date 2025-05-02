package com.moura.authorization.groups.repositories.specification;

import com.moura.authorization.groups.entities.Group;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;

public class GroupSpecification implements Specification<Group> {

    @Override
    public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return null;
    }

    public static Specification<Group> idIn(Set<UUID> ids) {
        return (root, query, cb) -> root.get("id").in(ids);
    }
}
