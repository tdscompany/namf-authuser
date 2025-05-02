package com.moura.authorization.users.controllers;

import com.moura.authorization.context.TenantContext;
import com.moura.authorization.specifications.SpecificationTemplate;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.users.mappers.UserMapper;
import com.moura.authorization.users.services.UserService;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
            SpecificationTemplate.UserSpec spec,
            @RequestParam(required = false) UUID groupId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Specification<User> finalSpec = SpecificationTemplate.tenantFilter();

        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        if (groupId != null) {
            finalSpec = finalSpec.and(SpecificationTemplate.userGroupId(groupId));
        }

        Page<User> users = userService.findAll(finalSpec, pageable);

        Page<UserDTO> dtoPage = userMapper.toDTOPage(users);

        return ResponseEntity.ok(dtoPage);
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


}
