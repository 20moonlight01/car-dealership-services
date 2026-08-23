package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.orders.models.OrderDto;
import ru.glebova.application.services.OrderService;
import ru.glebova.presentation.mapping.commands.PlaceConfiguredOrderMapper;
import ru.glebova.presentation.mapping.commands.PlaceStockOrderMapper;
import ru.glebova.presentation.mapping.dto.OrderMapper;
import ru.glebova.presentation.requests.PlaceConfiguredOrderRequest;
import ru.glebova.presentation.requests.PlaceStockOrderRequest;
import ru.glebova.presentation.requests.TryUpdateOrderStateRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Operations with orders (stock and configured)")
public class OrderController {
    private final OrderService orderService;
    private final PlaceStockOrderMapper placeStockOrderMapper;
    private final PlaceConfiguredOrderMapper placeConfiguredOrderMapper;
    private final OrderMapper orderMapper;

    public OrderController(
            OrderService orderService,
            PlaceStockOrderMapper placeStockOrderMapper,
            PlaceConfiguredOrderMapper placeConfiguredOrderMapper,
            OrderMapper orderMapper)
    {
        this.orderService = orderService;
        this.placeStockOrderMapper = placeStockOrderMapper;
        this.placeConfiguredOrderMapper = placeConfiguredOrderMapper;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about particular order")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public OrderDto getOrderInfo(@PathVariable UUID id) {
        return orderMapper.toDto(
                orderService.getOrderInfo(id));
    }

    @PostMapping("/stock")
    @Operation(summary = "Place order for particular car in stock")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public OrderDto placeStockOrder(@Valid @RequestBody PlaceStockOrderRequest request) {
        return orderMapper.toDto(
                orderService.placeStockOrder(placeStockOrderMapper.toCommand(request)));
    }

    @PostMapping("/configured")
    @Operation(summary = "Place order for custom car")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public OrderDto placeConfiguredOrder(@Valid @RequestBody PlaceConfiguredOrderRequest request) {
        return orderMapper.toDto(
                orderService.placeConfiguredOrder(placeConfiguredOrderMapper.toCommand(request)));
    }

    @GetMapping("/stock/all")
    @Operation(summary = "Get information about all stock orders")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<OrderDto> getStockOrderList() {
        return orderService.getStockOrderList()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @GetMapping("/configured/all")
    @Operation(summary = "Get information about all configured orders")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<OrderDto> getConfiguredOrderList() {
        return orderService.getConfiguredOrderList()
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve particular order")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public OrderDto tryApproveOrder(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryApproveOrder(id));
    }

    @PatchMapping("/{id}/mark-awaiting-pay")
    @Operation(summary = "Mark particular order awaiting pay")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public OrderDto tryMarkOrderAwaitingPay(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryMarkOrderAwaitingPay(id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel particular order")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public OrderDto tryCancelOrder(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryCancelOrder(id));
    }

    @PatchMapping("/{id}/mark-delivering")
    @Operation(summary = "Mark particular order delivering")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public OrderDto tryMarkOrderDelivering(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryMarkOrderDelivering(id));
    }

    @PatchMapping("/{id}/finish")
    @Operation(summary = "Finish particular order")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public OrderDto tryFinishOrder(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryFinishOrder(id));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Pay for particular order")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public OrderDto tryPayForOrder(
            @PathVariable UUID id,
            @Valid @RequestBody TryUpdateOrderStateRequest.TryPayForOrderRequest request)
    {
        return orderMapper.toDto(
                orderService.tryPayForOrder(id, request.getPayment()));
    }

    @PatchMapping("/{id}/mark-ready")
    @Operation(summary = "Mark particular order ready")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public OrderDto tryMarkOrderReady(@PathVariable UUID id)
    {
        return orderMapper.toDto(
                orderService.tryMarkOrderReady(id));
    }

    @GetMapping("/by-car/{id}/exists")
    @Operation(summary = "")
    public boolean isCarUsedInOrders(@PathVariable UUID id) {
        return orderService.isCarUsedInOrders(id);
    }
}
