package com.moura.authorization.groups.repositories;

import com.moura.authorization.groups.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    @Query("SELECT g.id FROM Permission g WHERE g.id IN :ids")
    Set<UUID> findExistingIds(@Param("ids") Set<UUID> ids);
}
