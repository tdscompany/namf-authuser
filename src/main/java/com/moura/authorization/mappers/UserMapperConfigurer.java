package com.moura.authorization.mappers;

import com.moura.authorization.configs.TypeMapConfigurer;
import com.moura.authorization.groups.mappers.converter.GroupIdToGroupConverter;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperConfigurer implements TypeMapConfigurer<UserDTO, User> {

    private final GroupIdToGroupConverter groupIdToGroupConverter;
    private final GroupToGroupDTOConverter groupToGroupDTOConverter;

    public UserMapperConfigurer(GroupIdToGroupConverter groupIdToGroupConverter,
                                GroupToGroupDTOConverter groupToGroupDTOConverter) {
        this.groupIdToGroupConverter = groupIdToGroupConverter;
        this.groupToGroupDTOConverter = groupToGroupDTOConverter;
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

        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(UserDTO::setPassword);
                    mapper.skip(UserDTO::setOldPassword);
                    mapper.using(groupToGroupDTOConverter).map(User::getGroups, UserDTO::setGroups);
                    mapper.skip(UserDTO::setGroupIds);
                });
    }

}