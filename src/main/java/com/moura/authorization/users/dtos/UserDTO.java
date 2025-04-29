package com.moura.authorization.users.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.validation.EmailConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    public interface UserView {
        public static interface RegistrationPost{}
        public static  interface PasswordPut {}
    }

    private UUID id;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView(UserView.RegistrationPost.class)
    @EmailConstraint(groups = {UserView.RegistrationPost.class})
    private String email;

    @NotBlank(groups = {UserView.RegistrationPost.class})
    @JsonView(UserView.RegistrationPost.class)
    private String password;

    @NotBlank(groups = UserView.PasswordPut.class)
    @JsonView(UserView.PasswordPut.class)
    private String oldPassword;

    @JsonView({UserView.RegistrationPost.class})
    private String name;

    @JsonView({UserView.RegistrationPost.class})
    private String description;
}
