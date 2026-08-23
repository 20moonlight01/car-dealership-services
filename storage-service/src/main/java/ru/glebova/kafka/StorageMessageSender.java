package ru.glebova.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.domain.messages.OutboxMessage;
import ru.glebova.infrastructure.persistence.repositories.StorageOutboxJpaRepository;

import java.time.Instant;

@Service
public class StorageMessageSender {
    private static final Logger log = LoggerFactory.getLogger(StorageMessageSender.class);

    private final StorageOutboxJpaRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public StorageMessageSender(
            StorageOutboxJpaRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate)
    {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMessage(OutboxMessage message) {
        try {
            kafkaTemplate.send("order-events", message.getPayload()).get();

            log.info("Outbox message published successfully, id: {}", message.getId());

            message.setProcessed(true);
            message.setProcessedAt(Instant.now());
            outboxRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to publish outbox message, id: {}", message.getId(), e);
        }
    }
}
