package com.moura.authorization.auth.entities;

import com.moura.authorization.users.entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users_credentials")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Credentials  {

    @Id
    @GeneratedValue(generator = "uuid7")
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean active;
}
