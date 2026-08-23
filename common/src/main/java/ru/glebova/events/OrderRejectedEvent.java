package ru.glebova.events;

import java.time.Instant;
import java.util.UUID;

public record OrderRejectedEvent(
        UUID eventId,
        String eventType,
        UUID orderId,
        String orderType,
        String traceId,
        String reason,
        Instant occurredAt)
{ }
