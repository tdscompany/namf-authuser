package com.moura.authorization.users.entities;

import com.moura.authorization.auth.entities.SecurityAuthority;
import com.moura.authorization.enums.UserStatus;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.utils.MessageUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Table(name = "users")
@Entity
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "uuid7")
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column()
    private UUID organizationId;

    @Column
    private String description;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Credentials> credentials;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return groups.stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(SecurityAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return credentials.stream().filter(Credentials::isActive).findFirst()
                .map(Credentials::getPassword)
                .orElseThrow(() -> new NoSuchElementException(MessageUtils.get("error.not_active_credentials")));
    }

    @Override
    public String getUsername() {
        return email;
    }

}
