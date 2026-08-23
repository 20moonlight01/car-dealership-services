package ru.glebova.application.services;

import org.springframework.stereotype.Service;
import ru.glebova.keycloak.KeycloakService;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class WarehouseAdminService {
    private final KeycloakService keycloakService;

    public WarehouseAdminService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    public List<UUID> findAllWarehouseAdminIds() {
        return keycloakService.getUserIdsByRole("WAREHOUSE_ADMIN");
    }

    public Optional<UUID> findRandomWarehouseAdminId() {
        var warehouseAdmins = findAllWarehouseAdminIds();
        if (warehouseAdmins.isEmpty())
            return Optional.empty();

        var random = new Random();
        return Optional.of(warehouseAdmins.get(random.nextInt(warehouseAdmins.size())));
    }
}
