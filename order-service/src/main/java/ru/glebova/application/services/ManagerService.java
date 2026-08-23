package ru.glebova.application.services;

import org.springframework.stereotype.Service;
import ru.glebova.keycloak.KeycloakService;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ManagerService {
    private final KeycloakService keycloakService;

    public ManagerService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public List<UUID> findAllManagerIds() {
        return keycloakService.getUserIdsByRole("MANAGER");
    }

    public Optional<UUID> findRandomManagerId() {
        var managers = findAllManagerIds();
        if (managers.isEmpty())
            return Optional.empty();

        var random = new Random();
        return Optional.of(managers.get(random.nextInt(managers.size())));
    }
}
