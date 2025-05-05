package com.moura.authorization.groups.services;

import java.util.Set;
import java.util.UUID;

public interface GroupService {
    boolean validateGroupIds(Set<UUID> groupsIds);
}
