package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.cars.PartModelCompatibility;

import java.util.List;
import java.util.UUID;

@Repository
public interface PartModelJpaRepository extends JpaRepository<PartModelCompatibility, UUID> {
    @Query("SELECT compatible.model.id FROM PartModelCompatibility compatible WHERE compatible.part.id = :partId")
    List<UUID> findCompatibleModelIdsByPartId(@Param("partId") UUID partId);

    default void softDeleteAllByModelId(UUID modelId) {
        findAll().stream()
                .filter(entity -> entity.getModel().getId().equals(modelId))
                .forEach(entity -> {
                    entity.setRemoved(true);
                    save(entity);
                });
    }
}
