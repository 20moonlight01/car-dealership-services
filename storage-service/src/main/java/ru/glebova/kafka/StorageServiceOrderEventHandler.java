package ru.glebova.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.assembly.operations.AddAssemblyOrderCommand;
import ru.glebova.application.services.AssemblyService;
import ru.glebova.domain.events.ProcessedEvent;
import ru.glebova.domain.messages.OutboxMessage;
import ru.glebova.domain.storageprocesses.AssemblyOrderState;
import ru.glebova.events.OrderRejectedEvent;
import ru.glebova.events.OrderSentForApprovalEvent;
import ru.glebova.infrastructure.persistence.repositories.StorageOutboxJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.StorageProcessedEventJpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class StorageServiceOrderEventHandler {
    private static final Logger log = LoggerFactory.getLogger(StorageServiceOrderEventHandler.class);

    private final AssemblyService assemblyService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final StorageOutboxJpaRepository outboxRepository;
    private final StorageProcessedEventJpaRepository processedEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public StorageServiceOrderEventHandler(
            AssemblyService assemblyService,
            KafkaTemplate<String, String> kafkaTemplate, StorageOutboxJpaRepository outboxRepository,
            StorageProcessedEventJpaRepository processedEventRepository)
    {
        this.assemblyService = assemblyService;
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
        this.processedEventRepository = processedEventRepository;
    }

    private void setupSystemSecurityContext() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        var auth = new UsernamePasswordAuthenticationToken("system", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "storage-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleOrderEvent(String message) {
        setupSystemSecurityContext();
        try {
            JsonNode node = objectMapper.readTree(message);
            String eventType = node.get("eventType").asText();

            String traceId = node.has("traceId") ? node.get("traceId").asText() : UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);

            if ("OrderSentForApproval".equals(eventType)) {
                OrderSentForApprovalEvent event = objectMapper.readValue(
                        message, OrderSentForApprovalEvent.class);
                handleOrderSentForApproval(event);
            }
        } catch (Exception e) {
            log.error("Failed to handle order event", e);
            throw new RuntimeException("Storage event processing failed", e);
        } finally {
            SecurityContextHolder.clearContext();
            MDC.remove("traceId");
        }
    }

    public void handleOrderSentForApproval(OrderSentForApprovalEvent event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.warn("Event already processed, skipping. eventId: {}, orderId: {}",
                    event.eventId(), event.orderId());
            return;
        }

        var command = new AddAssemblyOrderCommand(
                event.orderId(),
                event.orderType(),
                event.carId(),
                MDC.get("traceId"));

        // try {
            var assemblyOrder = assemblyService.addAssemblyOrder(command);

            if (assemblyOrder.getState() == AssemblyOrderState.FAIL) {
                log.warn("Assembly order FAILED for order: {}, reason: not enough parts", event.orderId());
                publishOrderRejected(event, "Not enough car parts in stock");
            } else {
                log.info("Assembly order CREATED for order: {}", event.orderId());
            }

            processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
//        } catch (RuntimeException e) {
//            log.error("Failed to process OrderSentForApproval for order: {}", event.orderId(), e);
//        }
    }

    private void publishOrderRejected(OrderSentForApprovalEvent event, String reason) {
        try {
            var orderRejectedEvent = new OrderRejectedEvent(
                    UUID.randomUUID(),
                    "OrderRejected",
                    event.orderId(),
                    event.orderType(),
                    event.traceId(),
                    reason,
                    Instant.now());

            String currentTraceId = MDC.get("traceId");

            var outbox = new OutboxMessage(
                    event.orderId(),
                    "OrderRejected",
                    objectMapper.writeValueAsString(orderRejectedEvent),
                    currentTraceId);

            outboxRepository.save(outbox);

            log.info("Successfully saved 'OrderRejected' event to outbox for orderId: {}", event.orderId());
            // kafkaTemplate.send("order-events", objectMapper.writeValueAsString(orderRejectedEvent));
            // log.info("OrderRejectedEvent published successfully for order: {}", event.orderId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize OrderRejectedEvent for order: {}", event.orderId(), e);
        }
//        catch (Exception e) {
//            log.error("Failed to save OrderRejectedEvent for order: {}", event.orderId(), e);
//        }
    }
}
