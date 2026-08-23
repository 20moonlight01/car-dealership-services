package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.carparts.CarPart;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarPartJpaRepository extends JpaRepository<CarPart, UUID> {
    List<CarPart> findByNameIn(List<String> names);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
