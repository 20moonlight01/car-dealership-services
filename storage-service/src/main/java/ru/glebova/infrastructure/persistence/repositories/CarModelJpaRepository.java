package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.cars.CarModel;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarModelJpaRepository extends
        JpaRepository<CarModel, UUID>,
        JpaSpecificationExecutor<CarModel>
{
    List<CarModel> findByBrandNameInAndNameIn(List<String> brandNames, List<String> names);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
