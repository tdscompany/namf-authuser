package com.moura.authorization.configs;

import com.moura.authorization.exceptions.MissingTenantIdException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.organization.repositories.OrganizationRepository;
import com.moura.authorization.users.entities.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantResolver {

    private final OrganizationRepository organizationRepository;

    public TenantResolver(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public UUID resolveTenant(User user, UUID tenantIdHeader) {
        if (user.getOrganizationId() != null) {
            return user.getOrganizationId();
        }

        if (tenantIdHeader == null) {
            throw new MissingTenantIdException("Tenant ID must be provided for superadmin users.");
        }

        if (!organizationRepository.existsById(tenantIdHeader)) {
            throw new NotFoundException("Tenant not found.");
        }

        return tenantIdHeader;
    }
}
