package ru.glebova.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KeycloakService {
    private final Keycloak keycloak;
    private final String realm;

    public KeycloakService(
            @Value("${keycloak.auth-server-url}") String serverUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.admin.username}") String username,
            @Value("${keycloak.admin.password}") String password,
            @Value("${keycloak.admin-client-id}") String clientId)
    {
        this.realm = realm;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .username(username)
                .password(password)
                .clientId(clientId)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    public List<UUID> getUserIdsByRole(String roleName) {
        return keycloak.realm(realm)
                .roles()
                .get(roleName)
                .getUserMembers()
                .stream()
                .map(user -> UUID.fromString(user.getId()))
                .toList();
    }
}
