package org.playground.scpapi.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private UUID uuid;
    private String name;
    private String email;
    private byte clearanceLevel;
    private LocalDateTime created;
}
