package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.event.entities.Event;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.repositories.EventRepository;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserToUserDTOConverter implements Converter<User, UserDTO> {

    private final EventRepository eventRepository;
    private final GroupToGroupDTOConverter groupToGroupDTOConverter;

    public UserToUserDTOConverter(EventRepository eventRepository,
                                  GroupToGroupDTOConverter groupToGroupDTOConverter) {
        this.eventRepository = eventRepository;
        this.groupToGroupDTOConverter = groupToGroupDTOConverter;
    }

    @Override
    public UserDTO convert(MappingContext<User, UserDTO> context) {
        User user = context.getSource();
        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setTelefone(user.getTelefone());
        dto.setDescription(user.getDescription());
        dto.setUserStatus(user.getUserStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setGroups(groupToGroupDTOConverter.convert(user.getGroups()));
        dto.setLastAccess(resolveLastAccess(user.getId()));

        return dto;
    }

    private LocalDateTime resolveLastAccess(UUID userId) {
        return eventRepository.findLatestByAuthorIdAndEventGroupAndEvent(
                        userId,
                        EventGroup.AUTH.name(),
                        EventType.AUTH_SUCCESS.name()
                )
                .map(Event::getTimestamp)
                .orElse(null);
    }
}