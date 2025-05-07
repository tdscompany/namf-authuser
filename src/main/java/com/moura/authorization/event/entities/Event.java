package com.moura.authorization.event.entities;

import com.moura.authorization.users.entities.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid7")
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column()
    private UUID authorId;

    @Column()
    private UUID organizationId;

    @Column()
    private String authorType;

    @Column()
    private LocalDateTime timestamp;

    @Column()
    private String event;

    @Column()
    private String eventGroup;

    @Column(columnDefinition = "TEXT")
    private String payload;

    public Event(LocalDateTime lastAccess) {
        this.timestamp = lastAccess;
    }
}
