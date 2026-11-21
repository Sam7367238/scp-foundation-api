package org.playground.scpapi.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.playground.scpapi.media.MediaNotFoundException;
import org.playground.scpapi.media.MediaRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/profile")
@AllArgsConstructor
public class ProfileController {
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/change-picture")
    public void changeProfilePicture(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ChangeProfilePictureRequest request) {
        var media = mediaRepository.findById(request.mediaUuid()).orElseThrow(MediaNotFoundException::new);
        var userUuid = UUID.fromString(jwt.getSubject());

        var user = userRepository.findById(userUuid).orElseGet(() -> userService.syncUserFromKeycloak(userUuid));

        user.setProfilePicture(media);

        userRepository.save(user);
    }
}
