package com.moura.authorization.groups.controllers;


import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.services.GroupService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Object> createGroup(
            @RequestBody @Validated(GroupDTO.GroupView.RegistrationPost.class)
            @JsonView(GroupDTO.GroupView.RegistrationPost.class) GroupDTO groupDto) {

        Group created = groupService.create(modelMapper.map(groupDto, Group.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, GroupDTO.class));
    }
}
