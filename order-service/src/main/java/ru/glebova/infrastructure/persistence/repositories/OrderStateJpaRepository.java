package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.orders.states.OrderStateBase;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderStateJpaRepository extends JpaRepository<OrderStateBase, UUID> {
    @Query(value = "SELECT * FROM order_states WHERE state_type = :type", nativeQuery = true)
    Optional<OrderStateBase> findByType(@Param("type") String type);
}
