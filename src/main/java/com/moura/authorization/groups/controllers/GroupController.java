package com.moura.authorization.groups.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.dtos.GroupFilterDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.specification.GroupSpecification;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.dtos.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/groups")
public class GroupController {

    private final GroupService groupService;


    private final ModelMapper modelMapper;

    public GroupController(GroupService groupService, ModelMapper modelMapper) {

        this.groupService = groupService;
        this.modelMapper = modelMapper;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<GroupDTO> createGroup(
            @RequestBody @Validated(GroupDTO.GroupView.RegistrationPost.class)
            @JsonView(GroupDTO.GroupView.RegistrationPost.class) GroupDTO groupDto) {

        Group created = groupService.create(modelMapper.map(groupDto, Group.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, GroupDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<GroupDTO>> getAllGroups(
            @ModelAttribute GroupFilterDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<GroupDTO> result = groupService.findAll(GroupSpecification.of(filter), pageable)
                .map(group -> modelMapper.map(group, GroupDTO.class));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Void> inactive(
            @PathVariable UUID groupId
    ) {
        groupService.inactivate(groupId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{groupId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserDTO> update(
            @PathVariable UUID groupId,
            @RequestBody @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDto
    ) {
        var group = groupService.findById(groupId);
        modelMapper.map(userDto,group);
        var updated = groupService.update(group);

        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(updated, UserDTO.class));
    }
}
