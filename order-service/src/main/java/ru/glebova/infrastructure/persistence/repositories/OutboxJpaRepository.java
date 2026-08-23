package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.messages.OutboxMessage;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findByProcessedFalse();

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
