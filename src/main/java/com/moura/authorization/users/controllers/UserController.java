package com.moura.authorization.users.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.groups.services.GroupService;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.dtos.UserFilterDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.repositories.specification.UserSpecification;
import com.moura.authorization.users.services.UserService;
import com.moura.authorization.utils.MessageUtils;
import org.modelmapper.Conditions;
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

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    private final GroupService groupService;

    private final ModelMapper modelMapper;

    public UserController(UserService userService, GroupService groupService, ModelMapper modelMapper) {
        this.userService = userService;
        this.groupService = groupService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> signup(
            @RequestBody @Validated(UserDTO.UserView.RegistrationPost.class)
            @JsonView(UserDTO.UserView.RegistrationPost.class) UserDTO userDto) {

        if (userService.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", MessageUtils.get("conflict.email_already_exists")));
        }

        if (!groupService.validateGroupIds(userDto.getGroupIds())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", MessageUtils.get("error.group_not_found")));
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPasswordNotEncoded(userDto.getPassword());

        User created = userService.create(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(created, UserDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @ModelAttribute UserFilterDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserDTO> result = userService.findAll(UserSpecification.of(filter), pageable)
                .map(user -> modelMapper.map(user, UserDTO.class));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> inactive(
            @PathVariable UUID userId
    ) {
        var userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", MessageUtils.get("error.user_not_found")));
        }

        var userModel = userModelOptional.get();
        userService.inactivate(userModel);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> update(
            @PathVariable UUID userId,
            @RequestBody @JsonView(UserDTO.UserView.UserPut.class) UserDTO userDto
    ) {
        var userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", MessageUtils.get("error.user_not_found")));
        }

        if (!groupService.validateGroupIds(userDto.getGroupIds())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", MessageUtils.get("error.group_not_found")));
        }

        var userModel = userModelOptional.get();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        modelMapper.map(userDto, userModel);

        var updated = userService.update(userModel);

        return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(updated, UserDTO.class));
    }

}
