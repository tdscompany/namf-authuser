package com.moura.authorization.users.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.enums.UserStatus;
import com.moura.authorization.groups.dtos.GroupDTO;
import com.moura.authorization.validation.EmailConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    public interface UserView {
        public static interface RegistrationPost{}
        public static  interface PasswordPut {}
        public static  interface UserPut {}
    }

    private UUID id;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    @EmailConstraint(groups = {UserView.RegistrationPost.class})
    private String email;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView(UserView.RegistrationPost.class)
    private String password;


    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private Set<UUID> groupIds;

    private Set<GroupDTO> groups;

    @JsonView({UserView.UserPut.class})
    private UserStatus userStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    @NotBlank(groups = UserView.PasswordPut.class)
    @JsonView(UserView.PasswordPut.class)
    private String oldPassword;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String name;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String telefone;

    @JsonView({UserView.RegistrationPost.class, UserView.UserPut.class})
    private String description;
}
