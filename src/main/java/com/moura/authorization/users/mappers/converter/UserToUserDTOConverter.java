package com.moura.authorization.users.mappers.converter;

import com.moura.authorization.event.entities.Event;
import com.moura.authorization.event.enums.EventGroup;
import com.moura.authorization.event.enums.EventType;
import com.moura.authorization.event.repositories.EventRepository;
import com.moura.authorization.groups.dtos.GroupOutputDTO;
import com.moura.authorization.groups.entities.Group;
import com.moura.authorization.users.dtos.UserOutputDTO;
import com.moura.authorization.users.entities.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserToUserDTOConverter implements Converter<User, UserOutputDTO> {

    private final EventRepository eventRepository;

    public UserToUserDTOConverter(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public UserOutputDTO convert(MappingContext<User, UserOutputDTO> context) {
        User user = context.getSource();

        Set<GroupOutputDTO> groupDTOs = user.getGroups().stream()
                .map(this::mapGroupWithoutPermissions)
                .collect(Collectors.toSet());

        return UserOutputDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .telefone(user.getTelefone())
                .description(user.getDescription())
                .userStatus(user.getUserStatus())
                .createdAt(user.getCreatedAt())
                .groups(groupDTOs)
                .lastAccess(resolveLastAccess(user.getId()))
                .build();
    }

    private GroupOutputDTO mapGroupWithoutPermissions(Group group) {
        return GroupOutputDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .color(group.getColor())
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