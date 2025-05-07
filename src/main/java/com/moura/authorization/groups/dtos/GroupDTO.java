package com.moura.authorization.groups.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.moura.authorization.groups.entities.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupDTO {

    public interface GroupView {
        public static interface RegistrationPost{}
        public static  interface GroupPut {}
    }

    private UUID id;
    @NotBlank(groups = {GroupView.RegistrationPost.class})
    @JsonView({GroupView.RegistrationPost.class,GroupView.GroupPut.class})
    private String name;

    @NotBlank(groups = {GroupView.RegistrationPost.class})
    @JsonView({GroupView.RegistrationPost.class,GroupView.GroupPut.class})
    private String color;

    @NotEmpty(groups = {GroupView.RegistrationPost.class})
    @JsonView({GroupView.RegistrationPost.class,GroupView.GroupPut.class})
    private Set<UUID> permissionIds;

    private Set<Permission> permissions;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public GroupDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

}
