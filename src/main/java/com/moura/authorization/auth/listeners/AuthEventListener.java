package com.moura.authorization.auth.listeners;

import com.moura.authorization.auth.event.AuthSuccessPayload;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.services.EventService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AuthEventListener {

    private final EventService eventService;

    public AuthEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @EventListener
    public void handleAuthSuccess(AuthSuccessPayload payload) {
        eventService.create(EventType.AUTH_SUCCESS, EventGroup.AUTH, payload);
    }
}
