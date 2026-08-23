package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.carparts.StockCarPart;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockCarPartJpaRepository extends JpaRepository<StockCarPart, UUID> {
    Optional<StockCarPart> findByCarPartId(UUID carPartId);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
