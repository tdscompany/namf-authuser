package com.moura.authorization.groups.services;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;


public interface GroupService {
    Group create(Group group);
    void validateGroupIds(Set<UUID> groupIds);
    Page<Group> findAll(Specification<Group> spec, Pageable pageable);
}
