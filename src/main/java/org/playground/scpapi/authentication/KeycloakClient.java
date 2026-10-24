package org.playground.scpapi.authentication;

import lombok.RequiredArgsConstructor;
import org.playground.scpapi.user.RegisterUserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureRestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        var form = new HashMap<>();
        form.put("grant_type", "client_credentials");
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);

        var res = restClient.post()
                .uri(baseUrl + "/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        assert res != null;

        return (String) res.get("access_token");
    }

    public void createUser(RegisterUserRequest request) {
        Map<String, Object> user = Map.of(
                "name", request.name(),
                "email", request.email(),
                "username", request.email(),
                "enabled", true,
                "attributes", Map.of(
                        "clearanceLevel", request.clearanceLevel(),
                        "name", request.name()
                ),
                "credentials", List.of(
                        Map.of(
                        "type", "password",
                        "value", request.temporaryPassword(),
                        "temporary", true)
                )
        );

        restClient.post()
                .uri(baseUrl + "/admin/realms/{realm}/users", realm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getServiceToken())
                .contentType(MediaType.APPLICATION_JSON)
                .body(user)
                .retrieve()
                .toBodilessEntity();
    }
}
