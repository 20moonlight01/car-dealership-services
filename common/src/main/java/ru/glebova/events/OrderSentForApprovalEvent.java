package ru.glebova.events;

import java.time.Instant;
import java.util.UUID;

public record OrderSentForApprovalEvent(
        UUID eventId,
        String eventType,
        UUID orderId,
        String orderType,
        String traceId,
        UUID carId,
        Instant occurredAt)
{ }
