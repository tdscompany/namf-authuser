package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.event.entities.Event;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.repositories.EventRepository;
import com.moura.authorization.groups.mappers.converter.GroupToGroupDTOConverter;
import com.moura.authorization.users.dtos.UserDTO;
import com.moura.authorization.users.dtos.UserOutputDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserToUserDTOConverter implements Converter<User, UserOutputDTO> {

    private final EventRepository eventRepository;
    private final GroupToGroupDTOConverter groupToGroupDTOConverter;

    public UserToUserDTOConverter(EventRepository eventRepository,
                                  GroupToGroupDTOConverter groupToGroupDTOConverter) {
        this.eventRepository = eventRepository;
        this.groupToGroupDTOConverter = groupToGroupDTOConverter;
    }

    @Override
    public UserOutputDTO convert(MappingContext<User, UserOutputDTO> context) {
        User user = context.getSource();

        return UserOutputDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .telefone(user.getTelefone())
                .description(user.getDescription())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .groups(groupToGroupDTOConverter.convert(user.getGroups()))
                .lastAccess(resolveLastAccess(user.getId()))
                .build();
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