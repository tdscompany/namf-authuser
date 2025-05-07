package com.moura.authorization.users.mappers;

import com.moura.authorization.configs.TypeMapConfigurer;
import com.moura.authorization.groups.mappers.converter.GroupIdToGroupConverter;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.mappers.converter.UserToUserDTOConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class UserMapperConfigurer implements TypeMapConfigurer<UserDTO, User> {

    private final GroupIdToGroupConverter groupIdToGroupConverter;

    public UserMapperConfigurer(GroupIdToGroupConverter groupIdToGroupConverter) {
        this.groupIdToGroupConverter = groupIdToGroupConverter;
    }

    @Override
    public void configure(ModelMapper modelMapper) {
        modelMapper.typeMap(UserDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.skip(User::setId);
                    mapper.using(groupIdToGroupConverter).map(UserDTO::getGroupIds, User::setGroups);
                    mapper.skip(User::setUserStatus);
                    mapper.skip(User::setCreatedBy);
                    mapper.skip(User::setUpdatedBy);
                    mapper.skip(User::setCreatedAt);
                    mapper.skip(User::setUpdatedAt);
                    mapper.skip(User::setCredentials);
                    mapper.skip(User::setOrganizationId);
                    mapper.skip(User::setPasswordNotEncoded);
                    mapper.skip(User::setVersion);
                });
    }
}