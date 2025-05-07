package com.moura.authorization.groups.entities;

import com.moura.authorization.users.entities.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(generator = "uuid7")
    private UUID id;


    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private UUID organizationId;


    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_permissions",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;

    @CreatedBy
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @LastModifiedBy
    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User updatedBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(updatable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    private Set<User> users;

    @Version
    private Long version;

    public Group() {
    }
    public Group(UUID uuid, String admin) {
        this.id = uuid;
        this.name = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    public Set<UUID> getPermissionsIds() {
        return permissions.stream().map(Permission::getId).collect(Collectors.toSet());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
