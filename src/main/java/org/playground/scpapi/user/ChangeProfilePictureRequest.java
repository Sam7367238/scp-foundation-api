package org.playground.scpapi.user;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChangeProfilePictureRequest(
        @NotNull(message = "Please fill this in")
        UUID mediaUuid
) {
}
