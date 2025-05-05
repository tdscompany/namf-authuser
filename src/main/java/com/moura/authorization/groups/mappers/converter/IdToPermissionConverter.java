package com.moura.authorization.groups.mappers.converter;

import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.entities.Permission;
import com.moura.authorization.groups.repositories.GroupRepository;
import com.moura.authorization.groups.repositories.PermissionRepository;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class IdToPermissionConverter implements Converter<Set<UUID>, Set<Permission>> {

    private final PermissionRepository permissionRepository;

    public IdToPermissionConverter(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Set<Permission> convert(MappingContext<Set<UUID>, Set<Permission>> context) {
        Set<UUID> permissionIds = context.getSource();
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }
}
