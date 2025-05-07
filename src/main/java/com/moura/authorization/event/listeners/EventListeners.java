package com.moura.authorization.event.listeners;

import com.moura.authorization.auth.event.AuthSuccessPayload;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.services.EventService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class EventListeners {
    private final EventService eventService;

    public EventListeners(EventService eventService) {
        this.eventService = eventService;
    }

    @EventListener
    public void handleAuthSuccess(AuthSuccessPayload payload) {
        eventService.create(EventType.AUTH_SUCCESS, EventGroup.AUTH, payload);
    }
}
