package ru.glebova.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.domain.messages.OutboxMessage;
import ru.glebova.infrastructure.persistence.repositories.OutboxJpaRepository;

import java.time.Instant;

@Component
public class OrderOutboxPublisher {
    private final OutboxJpaRepository outboxRepository;
    private final OrderMessageSender messageSender;

    public OrderOutboxPublisher(
            OutboxJpaRepository outboxRepository,
            OrderMessageSender messageSender)
    {
        this.outboxRepository = outboxRepository;
        this.messageSender = messageSender;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingMessages() {
        var messages = outboxRepository.findByProcessedFalse();

        for (OutboxMessage message : messages)
            messageSender.sendMessage(message);
    }
}
