package com.moura.authorization.users.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.users.dtos.*;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.specification.UserSpecification;
import com.moura.authorization.users.services.UserService;
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
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserOutputDTO> signup(
            @RequestBody @Validated RegistrationPostDTO registrationPostDTO) {

        User user = modelMapper.map(registrationPostDTO, User.class);
        user.setPasswordNotEncoded(registrationPostDTO.password());

        User created = userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, UserOutputDTO.class));
    }
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserOutputDTO> getById(
            @PathVariable UUID userId
    ) {
        var user = userService.findById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(user, UserOutputDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<UserOutputDTO>> getAllUsers(
            @ModelAttribute UserFilterDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserOutputDTO> result = userService.findAll(UserSpecification.of(filter), pageable)
                .map(user -> modelMapper.map(user, UserOutputDTO.class));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Void> inactive(
            @PathVariable UUID userId
    ) {
        var user = userService.findById(userId);
        userService.inactivate(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<UserOutputDTO> update(
            @PathVariable UUID userId,
            @RequestBody UserPutDTO userDto
    ) {
        User user = userService.findById(userId);
        modelMapper.map(userDto,user);
        User updated = userService.update(user);

        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(updated, UserOutputDTO.class));
    }

}
