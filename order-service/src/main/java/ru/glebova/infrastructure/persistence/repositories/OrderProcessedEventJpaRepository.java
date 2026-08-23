package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.events.ProcessedEvent;

import java.util.UUID;

@Repository
public interface OrderProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID> {
    boolean existsByEventId(UUID eventId);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
