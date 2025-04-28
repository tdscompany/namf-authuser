package com.moura.authorization.organization.repositories;

import com.moura.authorization.organization.entities.Organization;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrganizationRepository extends CrudRepository<Organization, UUID> {
}
