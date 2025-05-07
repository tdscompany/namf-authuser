package com.moura.authorization.event.repositories;

import com.moura.authorization.event.entities.Event;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("""
    SELECT e FROM Event e
    WHERE e.authorId = :authorId
      AND e.eventGroup = :eventGroup
      AND e.event = :event
    ORDER BY e.timestamp DESC
    """)
    Optional<Event> findLatestByAuthorIdAndEventGroupAndEvent(
            @Param("authorId") UUID authorId,
            @Param("eventGroup") String eventGroup,
            @Param("event") String event
    );
}
