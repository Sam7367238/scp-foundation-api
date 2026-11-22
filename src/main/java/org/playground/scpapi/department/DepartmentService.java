package org.playground.scpapi.department;

import lombok.AllArgsConstructor;
import org.playground.scpapi.media.MediaNotFoundException;
import org.playground.scpapi.media.MediaRepository;
import org.playground.scpapi.user.UserRepository;
import org.playground.scpapi.user.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class DepartmentService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final MediaRepository mediaRepository;
    private final DepartmentRepository departmentRepository;

    public Department createDepartment(UUID userUuid, CreateDepartmentRequest request) {
        var user = userRepository.findById(userUuid).orElseGet(() -> userService.syncUserFromKeycloak(userUuid));

        var department = new Department();
        department.setName(request.name());
        department.setUser(user);

        if (request.mediaUuid() != null) {
            var media = mediaRepository.findById(request.mediaUuid()).orElseThrow(MediaNotFoundException::new);
            department.setMedia(media);
        }

        return departmentRepository.save(department);
    }
}
