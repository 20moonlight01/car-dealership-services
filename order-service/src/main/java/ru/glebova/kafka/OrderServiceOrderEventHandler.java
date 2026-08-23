package ru.glebova.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.services.OrderService;
import ru.glebova.domain.events.ProcessedEvent;
import ru.glebova.events.OrderApprovedEvent;
import ru.glebova.events.OrderRejectedEvent;
import ru.glebova.infrastructure.persistence.repositories.OrderProcessedEventJpaRepository;

import java.time.Instant;
import java.util.List;

@Service
public class OrderServiceOrderEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderServiceOrderEventHandler.class);

    private final OrderService orderService;
    private final OrderProcessedEventJpaRepository processedEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public OrderServiceOrderEventHandler(OrderService orderService, OrderProcessedEventJpaRepository processedEventRepository) {
        this.orderService = orderService;
        this.processedEventRepository = processedEventRepository;
    }

    private void setupSystemSecurityContext() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        var auth = new UsernamePasswordAuthenticationToken("system", null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void handleOrderEvent(String message) {
        setupSystemSecurityContext();
        try {
            JsonNode node = objectMapper.readTree(message);
            String eventType = node.get("eventType").asText();

            if (node.has("traceId")) {
                MDC.put("traceId", node.get("traceId").asText());
            }

            if ("OrderApproved".equals(eventType)) {
                OrderApprovedEvent event = objectMapper.readValue(
                        message, OrderApprovedEvent.class);
                handleOrderApproved(event);
            } else if ("OrderRejected".equals(eventType)) {
                OrderRejectedEvent event = objectMapper.readValue(
                        message, OrderRejectedEvent.class);
                handleOrderRejected(event);
            }
        } catch (Exception e) {
            log.error("Failed to handle order event", e);
            throw new RuntimeException("Kafka message processing failed", e);
        } finally {
            SecurityContextHolder.clearContext();
            MDC.remove("traceId");
        }
    }

    private void handleOrderApproved(OrderApprovedEvent event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.warn("Event already processed, skipping. eventId: {}, orderId: {}",
                    event.eventId(), event.orderId());
            return;
        }

        //try {
            if (event.orderType().equals("STOCK")) {
                orderService.tryMarkOrderReady(event.orderId());
                log.info("Order marked as READY: {}", event.orderId());
            } else if (event.orderType().equals("CONFIGURED")) {
                orderService.tryMarkOrderDelivering(event.orderId());
                log.info("Order marked as DELIVERING: {}", event.orderId());
            }

            processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
//        } catch (RuntimeException e) {
//            log.error("Failed to process OrderApprovedEvent for order: {}", event.orderId(), e);
//        }
    }

    private void handleOrderRejected(OrderRejectedEvent event) {
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.warn("Event already processed, skipping. eventId: {}, orderId: {}",
                    event.eventId(), event.orderId());
            return;
        }

        //try {
            orderService.tryCancelOrder(event.orderId());
            log.info("Order cancelled: {}", event.orderId());

            processedEventRepository.save(new ProcessedEvent(event.eventId(), Instant.now()));
//        } catch (RuntimeException e) {
//            log.error("Failed to cancel order: {}", event.orderId(), e);
//        }
    }
}
