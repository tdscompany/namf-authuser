package com.moura.authorization.event.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moura.authorization.context.TenantContext;
import com.moura.authorization.event.entities.Event;
import com.moura.authorization.event.enums.AuthorType;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.repositories.EventRepository;
import com.moura.authorization.event.services.EventService;
import com.moura.authorization.exceptions.PayloadProcessingException;
import com.moura.authorization.users.entities.User;
import com.moura.authorization.utils.MessageUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    public EventServiceImpl(EventRepository eventRepository, ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void create(EventType type, EventGroup group, Object payload) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) return;

        String payloadString;
        try {
            payloadString = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new PayloadProcessingException(MessageUtils.get("error.event_payload_conversion"));
        }

        var event = Event.builder()
                .authorId(user.getId())
                .organizationId(TenantContext.getCurrentTenant())
                .authorType(AuthorType.USER.name())
                .timestamp(LocalDateTime.now())
                .event(type.name())
                .eventGroup(group.name())
                .payload(payloadString)
                .build();

        eventRepository.save(event);
    }
}
