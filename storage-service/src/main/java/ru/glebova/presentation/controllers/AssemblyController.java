package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.assembly.models.AssemblyOrderDto;
import ru.glebova.application.services.AssemblyService;
import ru.glebova.presentation.mapping.commands.AddAssemblyOrderMapper;
import ru.glebova.presentation.mapping.dto.AssemblyOrderMapper;
import ru.glebova.presentation.requests.AddAssemblyOrderRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assembly")
@Tag(name = "Assembly", description = "Assembly operations")
public class AssemblyController {
    private final AssemblyService assemblyService;
    private final AssemblyOrderMapper assemblyOrderMapper;
    private final AddAssemblyOrderMapper addAssemblyOrderMapper;

    public AssemblyController(
            AssemblyService assemblyService,
            AssemblyOrderMapper assemblyOrderMapper,
            AddAssemblyOrderMapper addAssemblyOrderMapper)
    {
        this.assemblyService = assemblyService;
        this.assemblyOrderMapper = assemblyOrderMapper;
        this.addAssemblyOrderMapper = addAssemblyOrderMapper;
    }

    @GetMapping("/orders/all")
    @Operation(summary = "Get information about all assembly orders")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public List<AssemblyOrderDto> getAssemblyOrderList() {
        return assemblyService.getAssemblyOrderList()
                .stream()
                .map(assemblyOrderMapper::toDto)
                .toList();
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Get information about particular assembly order")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public AssemblyOrderDto getAssemblyOrderInfo(@PathVariable UUID id) {
        return assemblyOrderMapper.toDto(
                assemblyService.getAssemblyOrderInfo(id));
    }

    @PostMapping("/orders")
    @Operation(summary = "Add information about particular assembly order")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public AssemblyOrderDto addAssemblyOrder(@Valid @RequestBody AddAssemblyOrderRequest request) {
        return assemblyOrderMapper.toDto(
                assemblyService.addAssemblyOrder(addAssemblyOrderMapper.toCommand(request)));
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Erase information about particular assembly order")
    @PreAuthorize("hasRole('ADMIN')")
    public AssemblyOrderDto removeAssemblyOrder(@PathVariable UUID id) {
        return assemblyOrderMapper.toDto(
                assemblyService.removeAssemblyOrder(id));
    }

    @PatchMapping("/orders/{id}/mark-assembled")
    @Operation(summary = "Mark particular assembly order assembled")
    @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN', 'ADMIN')")
    public AssemblyOrderDto tryMarkAssemblyOrderAssembled(@PathVariable UUID id) {
        return assemblyOrderMapper.toDto(
                assemblyService.tryMarkOrderAssembled(id));
    }
}
