package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.testdriverequests.TestDriveRequest;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestDriveRequestJpaRepository extends JpaRepository<TestDriveRequest, UUID> {
    List<TestDriveRequest> findByCarIdIn(List<UUID> carIds);

    List<TestDriveRequest> findByClientId(UUID clientId);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
