package org.playground.scpapi.common;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.playground.scpapi.user.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KeycloakService {
    private final Keycloak keycloakClient;

    @Value("${spring.keycloak.realm}")
    private String realm;

    public String createUser(RegisterUserRequest request, UUID profileId) {
            var passwordCredentials = new CredentialRepresentation();
            passwordCredentials.setType(CredentialRepresentation.PASSWORD);
            passwordCredentials.setValue(request.temporaryPassword());
            passwordCredentials.setTemporary(true);

            var userRepresentation = new UserRepresentation();
            userRepresentation.setEmail(request.email());
            userRepresentation.setUsername(request.email());

            userRepresentation.setAttributes(Map.of(
                    "clearanceLevel", List.of(Integer.toString(request.clearanceLevel())),
                    "name", List.of(request.name()),
                    "phoneNumber", List.of(request.phoneNumber()),
                    "profileId", List.of(profileId.toString())
            ));

            userRepresentation.setEnabled(true);
            userRepresentation.setCredentials(List.of(passwordCredentials));

            try (var response = keycloakClient.realm(realm).users().create(userRepresentation)) {
                return CreatedResponseUtil.getCreatedId(response);
            }
    }
}
