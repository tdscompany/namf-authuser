package com.moura.authorization.groups.services;

import com.moura.authorization.groups.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;
import java.util.UUID;


public interface GroupService {
    Group create(Group group);
    void validateGroupIds(Set<UUID> groupIds);
    Page<Group> findAll(Specification<Group> spec, Pageable pageable);
    void inactivate(UUID groupId);
    Group findById(UUID groupId);

    Group update(Group group);
}
