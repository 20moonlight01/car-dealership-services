package ru.glebova.events;

import java.time.Instant;
import java.util.UUID;

public record OrderApprovedEvent(
        UUID eventId,
        String eventType,
        UUID orderId,
        String orderType,
        String traceId,
        Instant occurredAt)
{ }
