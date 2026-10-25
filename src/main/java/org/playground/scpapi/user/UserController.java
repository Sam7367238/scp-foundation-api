package org.playground.scpapi.user;

import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.playground.scpapi.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterUserRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        var clearanceLevel = (Long) jwt.getClaim("clearanceLevel");

        if (clearanceLevel < 4) {
            throw new AccessDeniedException("You are not authorized to perform this action");
        }

        var dto = userService.createUser(request);

        var uri = uriBuilder.path("/users/{id}").buildAndExpand(dto.getUuid()).toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<ErrorDto> handleKeycloakError(WebApplicationException exception) {
        int status = exception.getResponse().getStatus();

        return switch (status) {
            case 409 -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorDto("This email address is taken"));

            case 400 -> {
                log.warn("Keycloak rejected the request (status {})", status, exception);
                yield ResponseEntity.badRequest()
                        .body(new ErrorDto("The user details were rejected. Check the values and try again."));
            }

            default -> {
                log.error("Keycloak call failed (status {})", status, exception);
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorDto("Something went wrong"));
            }
        };
    }
}
