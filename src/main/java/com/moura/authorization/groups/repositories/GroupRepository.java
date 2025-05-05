package com.moura.authorization.groups.repositories;

import com.moura.authorization.groups.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    @Query("SELECT g.id FROM Group g WHERE g.id IN :ids AND g.organizationId = :tenantId")
    Set<UUID> findExistingIdsByTenant(@Param("ids") Set<UUID> ids, @Param("tenantId") UUID tenantId);
}
