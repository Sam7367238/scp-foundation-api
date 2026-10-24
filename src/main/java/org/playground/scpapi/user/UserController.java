package org.playground.scpapi.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.playground.scpapi.common.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

/***
 * {
 *   "name": "string",
 *   "email": "user@example.com",
 *   "phoneNumber": "string",
 *   "clearanceLevel": 3,
 *   "department": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
 *   "buildingNumber": 0,
 *   "district": "string",
 *   "street": "string",
 *   "city": "string",
 *   "stateOrProvince": "string",
 *   "postalCode": 0,
 *   "country": "string",
 *   "temporaryPassword": "string"
 * }
 */

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

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ErrorDto> handleConflictException(RestClientResponseException exception) {
        if (exception.getStatusCode() == HttpStatus.CONFLICT) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDto("This email address is taken"));
        }

        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Something went wrong"));
    }
}
