package com.moura.authorization.groups.services;

import com.moura.authorization.groups.entities.Group;

import java.util.Set;
import java.util.UUID;


public interface GroupService {
    Group create(Group group);
    void validateGroupIds(Set<UUID> groupIds);
}
