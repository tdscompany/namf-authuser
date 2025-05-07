package com.moura.authorization.groups.repositories;

import com.moura.authorization.groups.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    @Query("SELECT g.id FROM Group g WHERE g.id IN :ids AND g.organizationId = :tenantId")
    Set<UUID> findExistingIdsByTenant(@Param("ids") Set<UUID> ids, @Param("tenantId") UUID tenantId);

    @Query("""
      SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
      FROM Group u
      WHERE u.name = :name AND u.organizationId = :tenantId
    """)
    boolean existsByName(String name, @Param("tenantId") UUID tenantId);

    @Query("""
      SELECT g FROM Group g
      WHERE g.id = :id AND g.organizationId = :currentTenant
    """)
    Optional<Group> findByIdAndTenant(UUID id, UUID currentTenant);

    @Modifying
    @Query(value = "DELETE FROM user_groups WHERE group_id = :groupId", nativeQuery = true)
    void deleteGroupAssociations(@Param("groupId") UUID groupId);
}
