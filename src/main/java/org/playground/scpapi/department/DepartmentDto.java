package org.playground.scpapi.department;

import java.util.UUID;

public record DepartmentDto(
        UUID uuid,
        String name,
        UUID mediaUuid
) {
}
