package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.cars.TestableCar;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TestableCarJpaRepository extends JpaRepository<TestableCar, UUID> {
    Optional<TestableCar> findByCarId(UUID carId);

    default void softDeleteByCarId(UUID carId) {
        findByCarId(carId).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
