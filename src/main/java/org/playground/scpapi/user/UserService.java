package org.playground.scpapi.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.playground.scpapi.common.KeycloakService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    private final UserMapper userMapper;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final KeycloakService keycloakClient;

    public UserDto createUser(RegisterUserRequest request) {
        var profile = new Profile();
        profileRepository.save(profile);

        var uuidString = keycloakClient.createKeycloakUser(request, profile.getUuid());
        var uuid = UUID.fromString(uuidString);

        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .uuid(uuid)
                .phoneNumber(request.phoneNumber())
                .clearanceLevel((byte) request.clearanceLevel())
                .profile(profile)
                .created(java.time.LocalDateTime.now())
                .build();

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto getUser(UUID uuid) {
        var user = userRepository.findById(uuid).orElseGet(() -> syncUserFromKeycloak(uuid));

        return userMapper.toDto(user);
    }

    private User syncUserFromKeycloak(UUID uuid) {
        var keycloakUser = keycloakClient.getUser(uuid);

        var attributes = keycloakUser.getAttributes();

        var profileIdString = attributes.get("profileId").getFirst();
        var profileId = UUID.fromString(profileIdString);
        var profile = profileRepository.findById(profileId)
                .orElseGet(() -> {
                    var newProfile = new Profile();
                    newProfile.setUuid(profileId);
                    return profileRepository.save(newProfile);
                });

        var clearanceLevel = Byte.parseByte(attributes.get("clearanceLevel").getFirst());
        var phoneNumber = attributes.get("phoneNumber").getFirst();

        var user = User.builder()
                .uuid(uuid)
                .name(attributes.get("name").getFirst())
                .email(keycloakUser.getEmail())
                .phoneNumber(phoneNumber)
                .clearanceLevel(clearanceLevel)
                .profile(profile)
                .created(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }
}
