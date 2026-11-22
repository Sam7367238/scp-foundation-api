package org.playground.scpapi.department;

import jakarta.validation.Valid;
import org.playground.scpapi.media.MediaNotFoundException;
import org.playground.scpapi.media.MediaRepository;
import org.playground.scpapi.user.UserRepository;
import org.playground.scpapi.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/departments")
public class DepartmentController {
    private final MediaRepository mediaRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentService departmentService;

    public DepartmentController(MediaRepository mediaRepository, UserRepository userRepository, UserService userService, DepartmentRepository departmentRepository, DepartmentMapper departmentMapper, DepartmentService departmentService) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateDepartmentRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var userUuid = UUID.fromString(jwt.getSubject());
        var clearanceLevel = (Long) jwt.getClaim("clearanceLevel");

        if (clearanceLevel < 4) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }

        var department = departmentService.createDepartment(userUuid, request);

        var uri = uriBuilder.path("/departments/{uuid}").buildAndExpand(department.getUuid()).toUri();

        return ResponseEntity.created(uri).body(departmentMapper.toDto(department));
    }
}
