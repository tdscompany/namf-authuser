package com.moura.authorization.groups.repositories;

import com.moura.authorization.groups.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID>, JpaSpecificationExecutor<Group> {

}
