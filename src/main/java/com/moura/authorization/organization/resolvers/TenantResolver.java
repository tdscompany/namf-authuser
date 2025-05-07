package com.moura.authorization.organization.resolvers;

import com.moura.authorization.exceptions.MissingTenantIdException;
import com.moura.authorization.exceptions.NotFoundException;
import com.moura.authorization.organization.repositories.OrganizationRepository;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.utils.MessageUtils;
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
            throw new MissingTenantIdException(MessageUtils.get("error.missing_tenant_id"));
        }

        if (!organizationRepository.existsById(tenantIdHeader)) {
            throw new NotFoundException(MessageUtils.get("error.tenant_not_found"));
        }

        return tenantIdHeader;
    }
}
