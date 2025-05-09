package com.moura.authorization.users.dtos;

import com.moura.authorization.users.enums.UserStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class UserFilterDTO {
    private String name;
    private String email;
    private UserStatus userStatus;
    private UUID groupId;
}
