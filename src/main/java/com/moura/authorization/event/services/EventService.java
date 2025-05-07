package com.moura.authorization.event.services;

import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;

public interface EventService {
    void create(EventType eventType, EventGroup eventGroup, Object payload);
}
