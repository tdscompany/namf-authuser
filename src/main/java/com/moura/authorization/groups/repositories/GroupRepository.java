package com.moura.authorization.groups.repositories;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.users.entities.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {
    @Query("SELECT g FROM Group g WHERE g.id IN :ids AND g.organizationId = :tenantId")
    Set<Group> findExistingIdsByTenant(@Param("ids") Set<UUID> ids, @Param("tenantId") UUID tenantId);

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

    @Query("""
      SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END
      FROM Group g
      WHERE g.name = :name AND g.id <> :currentGroupId AND g.organizationId = :currentTenant
    """)
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("currentGroupId") UUID currentGroupId, @Param("currentTenant") UUID currentTenant);

    @EntityGraph(attributePaths = {"permissions"})
    @Query("SELECT u FROM Group u WHERE u.id IN :ids")
    List<Group> findAllWithPermissionsByIds(@Param("ids") List<UUID> ids);
}
