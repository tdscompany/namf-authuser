package com.moura.authorization.users.controllers;

import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.dtos.UserFilterDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.mappers.UserMapper;
import com.moura.authorization.users.repositories.specification.UserSpecification;
import com.moura.authorization.users.services.UserService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @ModelAttribute UserFilterDTO filter,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<User> users = userService.findAll(UserSpecification.of(filter), pageable);
        return ResponseEntity.ok(userMapper.toDTOPage(users));
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
            @RequestBody UserDTO userDto
    ) {
        var userModelOptional = userService.findById(userId);
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", MessageUtils.get("error.user_not_found")));
        }

        var userModel = userModelOptional.get();
        userMapper.updateEntityFromDTO(userDto, userModel);

        var updatedUser = userService.update(userModel);

        return ResponseEntity.ok(userMapper.toDTO(updatedUser));
    }

}
