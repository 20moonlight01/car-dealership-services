package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.cars.Car;
import ru.glebova.domain.cars.CarType;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarJpaRepository extends JpaRepository<Car, UUID> {
    @Query("SELECT car FROM Car car WHERE car.carType = :type")
    List<Car> findAllCarsByType(@Param("type") CarType type);

    List<Car> findByModelIdIn(List<UUID> modelIds);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
