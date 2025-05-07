package com.moura.authorization.groups.listeners;

import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.services.EventService;
import com.moura.authorization.groups.event.GroupCreatedPayload;
import com.moura.authorization.groups.event.GroupDeletedPayload;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GroupEventListener {

    private final EventService eventService;

    public GroupEventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @EventListener
    public void handleGroupCreated(GroupCreatedPayload payload) {
        eventService.create(EventType.GROUP_CREATED, EventGroup.GROUP, payload);
    }

    @EventListener
    public void handleGroupDeleted(GroupDeletedPayload payload) {
        eventService.create(EventType.GROUP_DELETED, EventGroup.GROUP, payload);
    }
}
