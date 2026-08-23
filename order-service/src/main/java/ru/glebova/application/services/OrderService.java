package ru.glebova.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.orders.operations.PlaceConfiguredOrderCommand;
import ru.glebova.application.contracts.orders.operations.PlaceStockOrderCommand;
import ru.glebova.clients.StorageServiceClient;
import ru.glebova.domain.messages.OutboxMessage;
import ru.glebova.events.OrderSentForApprovalEvent;
import ru.glebova.exceptions.DomainValidationException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.exceptions.IncompatibleComponentException;
import ru.glebova.exceptions.NotEnoughRightsException;
import ru.glebova.domain.orders.ConfiguredOrder;
import ru.glebova.domain.orders.OrderBase;
import ru.glebova.domain.orders.OrderCore;
import ru.glebova.domain.orders.StockOrder;
import ru.glebova.domain.orders.states.*;
import ru.glebova.infrastructure.persistence.repositories.OrderJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.OrderStateJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.OutboxJpaRepository;
import ru.glebova.kafka.OrderOutboxPublisher;
import ru.glebova.valueobjects.Price;
import ru.glebova.utils.SecurityUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final StorageServiceClient storageServiceClient;
    private final OrderJpaRepository orderRepository;
    private final OrderStateJpaRepository orderStateRepository;
    private final OutboxJpaRepository outboxRepository;
    private final ManagerService managerService;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public OrderService(
            StorageServiceClient storageServiceClient,
            OrderJpaRepository orderRepository,
            OrderStateJpaRepository orderStateRepository,
            OutboxJpaRepository outboxRepository,
            ManagerService managerService)
    {
        this.storageServiceClient = storageServiceClient;
        this.orderRepository = orderRepository;
        this.orderStateRepository = orderStateRepository;
        this.outboxRepository = outboxRepository;
        this.managerService = managerService;
    }

    @Transactional(readOnly = true)
    public OrderBase getOrderInfo(UUID id) throws EntityNotFoundException {
        var order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin() && !SecurityUtils.currentIsManager()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getClientId().equals(userId))
                throw new NotEnoughRightsException("User can only see their own orders");
        }

        return order;
    }

    public StockOrder placeStockOrder(PlaceStockOrderCommand command)
            throws EntityNotFoundException
    {
        var clientId = SecurityUtils.getCurrentUserId();

        var carInfo = storageServiceClient.isExistingStockCar(command.carId());
        if (carInfo == null)
            throw new EntityNotFoundException("Car with such id does not exist");

        var managerId = managerService.findRandomManagerId()
                .orElseThrow(()
                        -> new EntityNotFoundException("No managers to process order"));

        var state = orderStateRepository.findByType("PLACED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        var order = new StockOrder(
                new OrderCore(
                        clientId,
                        managerId,
                        command.carId(),
                        state,
                        new Price(carInfo.price())));
        order = orderRepository.save(order);

        return order;
    }

    public ConfiguredOrder placeConfiguredOrder(PlaceConfiguredOrderCommand command)
            throws EntityNotFoundException, IncompatibleComponentException
    {
        var clientId = SecurityUtils.getCurrentUserId();

        var carInfo = storageServiceClient.addConfiguredCar(
                command.modelId(),
                command.newPartIds(),
                command.color());
        if (carInfo == null)
            throw new DomainValidationException("Impossible to create car");

        var managerId = managerService.findRandomManagerId()
                .orElseThrow(()
                        -> new EntityNotFoundException("No managers to process order"));

        var state = orderStateRepository.findByType("PLACED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        var order = new ConfiguredOrder(
                new OrderCore(
                        clientId,
                        managerId,
                        carInfo.id(),
                        state,
                        new Price(carInfo.price())));
        order = orderRepository.save(order);

        return order;
    }

    @Transactional(readOnly = true)
    public List<OrderBase> getStockOrderList() {
        if (SecurityUtils.currentIsAdmin() || SecurityUtils.currentIsManager()) {
            return orderRepository.findAllStockOrders();
        }

        var userId = SecurityUtils.getCurrentUserId();

        return orderRepository.findAllStockOrdersByClientId(userId);
    }

    @Transactional(readOnly = true)
    public List<OrderBase> getConfiguredOrderList() {
        if (SecurityUtils.currentIsAdmin() || SecurityUtils.currentIsManager()) {
            return orderRepository.findAllConfiguredOrders();
        }

        var userId = SecurityUtils.getCurrentUserId();

        return orderRepository.findAllConfiguredOrdersByClientId(userId);
    }

    public OrderBase tryApproveOrder(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getManagerId().equals(userId))
                throw new NotEnoughRightsException("Manager can only update state of orders assigned to them");
        }

        var state = orderStateRepository.findByType("APPROVED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryApprove((ApprovedState)state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    public OrderBase tryMarkOrderAwaitingPay(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getManagerId().equals(userId))
                throw new NotEnoughRightsException("Manager can only update state of orders assigned to them");
        }

        var state = orderStateRepository.findByType("AWAITING_PAY")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryMarkAwaitingPay((AwaitingPayState) state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    public OrderBase tryCancelOrder(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!((SecurityUtils.currentIsManager() && order.getManagerId().equals(userId))
                    || (SecurityUtils.currentIsUser() && order.getClientId().equals(userId))))
                throw new NotEnoughRightsException("Not enough rights to cancel this order");
        }

        var state = orderStateRepository.findByType("CANCELLED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryCancel((CancelledState) state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    public OrderBase tryMarkOrderDelivering(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getManagerId().equals(userId))
                throw new NotEnoughRightsException("Manager can only update state of orders assigned to them");
        }

        if (!(order instanceof ConfiguredOrder))
            throw new DomainValidationException("Order has no such state");

        var state = orderStateRepository.findByType("DELIVERING")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!((ConfiguredOrder) order).tryMarkDelivering((DeliveringState) state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    public OrderBase tryFinishOrder(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getManagerId().equals(userId))
                throw new NotEnoughRightsException("Manager can only update state of orders assigned to them");
        }

        var state = orderStateRepository.findByType("FINISHED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryFinish((FinishedState) state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    public OrderBase tryPayForOrder(UUID orderId, float payment)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getClientId().equals(userId))
                throw new NotEnoughRightsException("User can only update state of their orders");
        }

        var state = orderStateRepository.findByType("PAYED")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryPay((PayedState) state, new Price(payment)))
            throw new DomainValidationException("Payment failed or impossible");

        publishOrderSentForApprovalEvent(order);

        return order;
    }

    private void publishOrderSentForApprovalEvent(OrderBase order) {
        String orderType = "";

        if (order instanceof StockOrder) {
            orderType = "STOCK";
        } else if (order instanceof ConfiguredOrder) {
            orderType = "CONFIGURED";
        }

        String currentTraceId = MDC.get("traceId");
        if (currentTraceId == null)
            currentTraceId = UUID.randomUUID().toString().substring(0, 8);

        log.info("Preparing 'OrderSentForApproval' event for orderId: {}, type: {}, traceId: {}",
                order.getId(), orderType, currentTraceId);

        var event = new OrderSentForApprovalEvent(
                UUID.randomUUID(),
                "OrderSentForApproval",
                order.getId(),
                orderType,
                currentTraceId,
                order.getCarId(),
                Instant.now());

        try {
            var outbox = new OutboxMessage(
                    order.getId(),
                    "OrderSentForApproval",
                    objectMapper.writeValueAsString(event),
                    currentTraceId);
            outboxRepository.save(outbox);

            log.info("Successfully saved 'OrderSentForApproval' event to outbox for orderId: {}", order.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize 'OrderSentForApproval' event for orderId: {}. Event data: {}",
                    order.getId(), event, e);
            throw new RuntimeException("Failed to serialize event", e);
        } catch (Exception e) {
            log.error("Failed to save outbox message to database for orderId: {}", order.getId(), e);
            throw e;
        }
    }

    public OrderBase tryMarkOrderReady(UUID orderId)
            throws EntityNotFoundException, DomainValidationException
    {
        var order = orderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getManagerId().equals(userId))
                throw new NotEnoughRightsException("Manager can only update state of orders assigned to them");
        }

        var state = orderStateRepository.findByType("READY")
                .orElseThrow(() ->
                        new EntityNotFoundException("State not found"));

        if (!order.tryMarkReady((ReadyState) state))
            throw new DomainValidationException("Transition between these states is impossible");

        return order;
    }

    @Transactional(readOnly = true)
    public boolean isCarUsedInOrders(UUID carId) {
        return !orderRepository.findByCarIdIn(List.of(carId)).isEmpty();
    }
}
