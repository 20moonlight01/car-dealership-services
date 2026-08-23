package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.cars.models.StockCarPartDto;
import ru.glebova.application.services.InventoryService;
import ru.glebova.presentation.mapping.dto.StockCarPartMapper;
import ru.glebova.presentation.requests.ChangeCarPartQuantityRequest;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Inventory operations")
public class InventoryController {
    private final InventoryService inventoryService;
    private final StockCarPartMapper stockCarPartMapper;

    public InventoryController(
            InventoryService inventoryService,
            StockCarPartMapper stockCarPartMapper)
    {
        this.inventoryService = inventoryService;
        this.stockCarPartMapper = stockCarPartMapper;
    }

    @PatchMapping("/car-parts/{id}/replenish")
    @Operation(summary = "Replenish car part quantity")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public StockCarPartDto replenishCarPart(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeCarPartQuantityRequest request)
    {
        return stockCarPartMapper.toDto(
                inventoryService.replenishCarPart(id, request.quantity()));
    }

    @PatchMapping("/car-parts/{id}/reserve")
    @Operation(summary = "Reserve car part quantity")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public StockCarPartDto reserveCarPart(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeCarPartQuantityRequest request)
    {
        return stockCarPartMapper.toDto(
                inventoryService.reserveCarPart(id, request.quantity()));
    }

    @PatchMapping("/car-parts/{id}/unreserve")
    @Operation(summary = "Unreserve car part quantity")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public StockCarPartDto unreserveCarPart(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeCarPartQuantityRequest request)
    {
        return stockCarPartMapper.toDto(
                inventoryService.unreserveCarPart(id, request.quantity()));
    }

    @PatchMapping("/car-parts/{id}/use")
    @Operation(summary = "Use car part quantity")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public StockCarPartDto useCarPart(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeCarPartQuantityRequest request)
    {
        return stockCarPartMapper.toDto(
                inventoryService.useCarPart(id, request.quantity()));
    }
}
