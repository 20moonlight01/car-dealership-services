package ru.glebova.application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.assembly.operations.AddAssemblyOrderCommand;
import ru.glebova.domain.cars.CarType;
import ru.glebova.domain.messages.OutboxMessage;
import ru.glebova.domain.storageprocesses.AssemblyOrder;
import ru.glebova.domain.storageprocesses.AssemblyOrderState;
import ru.glebova.events.OrderApprovedEvent;
import ru.glebova.exceptions.DomainValidationException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.exceptions.NotEnoughRightsException;
import ru.glebova.infrastructure.persistence.repositories.AssemblyOrderJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.CarJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.StorageOutboxJpaRepository;
import ru.glebova.utils.SecurityUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AssemblyService {
    private static final Logger log = LoggerFactory.getLogger(AssemblyService.class);

    private final InventoryService inventoryService;
    private final WarehouseAdminService warehouseAdminService;
    private final CarJpaRepository carRepository;
    private final AssemblyOrderJpaRepository assemblyOrderRepository;
    private final StorageOutboxJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public AssemblyService(
            InventoryService inventoryService,
            WarehouseAdminService warehouseAdminService,
            CarJpaRepository carRepository,
            AssemblyOrderJpaRepository assemblyOrderRepository, StorageOutboxJpaRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate)
    {
        this.inventoryService = inventoryService;
        this.warehouseAdminService = warehouseAdminService;
        this.carRepository = carRepository;
        this.assemblyOrderRepository = assemblyOrderRepository;
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public List<AssemblyOrder> getAssemblyOrderList() {
        return assemblyOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AssemblyOrder getAssemblyOrderInfo(UUID id) throws EntityNotFoundException {
        return assemblyOrderRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Assembly order with such id does not exist"));
    }

    public AssemblyOrder addAssemblyOrder(AddAssemblyOrderCommand command) {
        var car = carRepository.findById(command.carId())
                .orElseThrow(()
                        -> new EntityNotFoundException("Car with such id does not exist"));

        var warehouseAdminId = warehouseAdminService.findRandomWarehouseAdminId()
                .orElseThrow(()
                        -> new EntityNotFoundException("No warehouse admins to process order"));

        var order = new AssemblyOrder(
                command.sourceOrderId(),
                command.sourceOrderType(),
                null,
                warehouseAdminId,
                car,
                command.traceId());

        if (car.getCarType() == CarType.CONFIGURED) {
            var carConfiguration = car.getConfiguration();
            var carPartIds = List.of(
                    carConfiguration.getSteeringWheel().getId(),
                    carConfiguration.getWheels().getId(),
                    carConfiguration.getInterior().getId(),
                    carConfiguration.getTransmission().getId());
            boolean allCarPartsAreAvailable = true;

            for (UUID partId : carPartIds)
                allCarPartsAreAvailable &= inventoryService.carPartIsAvailable(partId, 1);

            if (!allCarPartsAreAvailable) {
                order.setState(AssemblyOrderState.FAIL);
            } else {
                for (UUID partId : carPartIds)
                    inventoryService.reserveCarPart(partId, 1);
            }
        }

        if (order.getState() == null)
            order.setState(AssemblyOrderState.CREATED);

        order = assemblyOrderRepository.save(order);

        return order;
    }

    public AssemblyOrder removeAssemblyOrder(UUID id) {
        var order = assemblyOrderRepository.findById(id);
        if (order.isEmpty())
            throw new EntityNotFoundException("Assembly order with such id does not exist");

        assemblyOrderRepository.softDeleteById(id);

        return order.get();
    }

    public AssemblyOrder tryMarkOrderAssembled(UUID orderId) {
        var order = assemblyOrderRepository.findById(orderId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Assembly order with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!order.getWarehouseAdminId().equals(userId))
                throw new NotEnoughRightsException("Warehouse admin can only update state of orders assigned to them");
        }

        if (order.getState() != AssemblyOrderState.CREATED)
            throw new DomainValidationException("Transition between these states is impossible");

        order.setState(AssemblyOrderState.ASSEMBLED);

        publishOrderApproved(order);

        return order;
    }

    private void publishOrderApproved(AssemblyOrder order) {
        try {
            var orderApprovedEvent = new OrderApprovedEvent(
                    UUID.randomUUID(),
                    "OrderApproved",
                    order.getSourceOrderId(),
                    order.getSourceOrderType(),
                    order.getTraceId(),
                    Instant.now());

            String currentTraceId = order.getTraceId();

            var outbox = new OutboxMessage(
                    order.getSourceOrderId(),
                    "OrderRejected",
                    objectMapper.writeValueAsString(orderApprovedEvent),
                    currentTraceId);

            outboxRepository.save(outbox);

            log.info("Successfully saved 'OrderApproved' event to outbox for orderId: {}", order.getSourceOrderId());

            // kafkaTemplate.send("order-events", objectMapper.writeValueAsString(orderApprovedEvent));
            // log.info("OrderApprovedEvent published successfully for order: {}", order.getSourceOrderId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize OrderApprovedEvent for order: {}", order.getSourceOrderId(), e);
        } catch (Exception e) {
            log.error("Failed to save OrderApprovedEvent for order: {}", order.getSourceOrderId(), e);
        }
    }
}
