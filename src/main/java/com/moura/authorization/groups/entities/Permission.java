package com.moura.authorization.groups.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(generator = "uuid7")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;


}
