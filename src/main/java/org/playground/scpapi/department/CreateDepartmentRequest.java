package org.playground.scpapi.department;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public record CreateDepartmentRequest(
        @NotBlank(message = "Please fill this in")
        @Length(min = 3, max = 255, message = "This must be between 3 and 255 characters")
    String name,
    UUID mediaUuid
) {
}
