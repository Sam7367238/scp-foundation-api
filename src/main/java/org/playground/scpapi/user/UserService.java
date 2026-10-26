package org.playground.scpapi.user;

import lombok.RequiredArgsConstructor;
import org.playground.scpapi.common.KeycloakService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final ProfileRepository profileRepository;
    private final KeycloakService keycloakClient;

    public UserDto createUser(RegisterUserRequest request) {
        var user = userMapper.toEntity(request);

        Profile profile = new Profile();
        user.setProfile(profile);

        profileRepository.save(profile);

        var uuid = UUID.fromString(keycloakClient.createUser(request, profile.getUuid()));

        var dto = userMapper.toDto(user);

        dto.setUuid(uuid);

        return dto;
    }
}
