package com.moura.authorization.groups.controllers;

import com.moura.authorization.groups.dtos.*;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.groups.repositories.specification.GroupSpecification;
import com.moura.authorization.groups.services.GroupService;
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
    public ResponseEntity<GroupOutputDTO> createGroup(
            @RequestBody @Validated GroupRegistrationDTO groupDto) {

        Group created = groupService.create(modelMapper.map(groupDto, Group.class));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, GroupOutputDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<GroupOutputDTO>> getAllGroups(
            @ModelAttribute GroupFilterDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<GroupOutputDTO> result = groupService.findAll(GroupSpecification.of(filter), pageable)
                .map(group -> modelMapper.map(group, GroupOutputDTO.class));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<GroupOutputDTO> getById(
            @PathVariable UUID groupId
    ) {
        var group = groupService.findById(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(group, GroupOutputDTO.class));
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
    public ResponseEntity<GroupOutputDTO> update(
            @PathVariable UUID groupId,
            @RequestBody GroupPutDTO groupDto
    ) {
        Group group = groupService.findById(groupId);
        modelMapper.map(groupDto,group);
        Group updated = groupService.update(group);

        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(updated, GroupOutputDTO.class));
    }
}
