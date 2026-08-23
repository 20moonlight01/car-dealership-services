package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.storageprocesses.AssemblyOrder;

import java.util.UUID;

@Repository
public interface AssemblyOrderJpaRepository extends JpaRepository<AssemblyOrder, UUID> {
    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
