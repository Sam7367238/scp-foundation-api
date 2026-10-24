package org.playground.scpapi.authentication;

import lombok.RequiredArgsConstructor;
import org.playground.scpapi.user.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureRestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@AutoConfigureRestClient
public class KeycloakClient {
    private final RestClient restClient = RestClient.create();

    @Value("${spring.keycloak.base-url}")
    private String baseUrl;

    @Value("${spring.keycloak.realm}")
    private String realm;

    @Value("${spring.keycloak.client-id}")
    private String clientId;

    @Value("${spring.keycloak.client-secret}")
    private String clientSecret;

    private String getServiceToken() {
        var form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        var response = restClient.post()
                .uri(baseUrl + "/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        assert response != null;

        return (String) response.get("access_token");
    }

    public String createUser(RegisterUserRequest request, UUID profileId) {
        Map<String, Object> user = Map.of(
                "email", request.email(),
                "username", request.email(),
                "enabled", true,
                "attributes", Map.of(
                        "clearanceLevel", request.clearanceLevel(),
                        "name", request.name(),
                        "phoneNumber", request.phoneNumber(),
                        "profileId", profileId.toString()
                ),
                "credentials", List.of(
                        Map.of(
                        "type", "password",
                        "value", request.temporaryPassword(),
                        "temporary", true)
                )
        );

        ResponseEntity<Void> response = restClient.post()
                .uri(baseUrl + "/admin/realms/{realm}/users", realm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toBodilessEntity();

        var location = response.getHeaders().getLocation();

        if (location == null) {
            throw new IllegalStateException("Keycloak did not return a Location header");
        }

        String path = location.getPath();

        return path.substring(path.lastIndexOf('/') + 1);
    }
}
