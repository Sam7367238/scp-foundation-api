package org.playground.scpapi.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var dto = userService.createUser(request);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(dto.uuid()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public Object getInformation() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }
}
