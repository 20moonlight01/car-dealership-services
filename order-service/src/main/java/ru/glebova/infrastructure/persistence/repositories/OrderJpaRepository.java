package ru.glebova.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.glebova.domain.orders.OrderBase;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderBase, UUID> {
    @Query("SELECT o FROM StockOrder o")
    List<OrderBase> findAllStockOrders();

    @Query("SELECT o FROM ConfiguredOrder o")
    List<OrderBase> findAllConfiguredOrders();

    @Query("SELECT o FROM StockOrder o WHERE o.orderCore.clientId = :clientId")
    List<OrderBase> findAllStockOrdersByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT o FROM ConfiguredOrder o WHERE o.orderCore.clientId = :clientId")
    List<OrderBase> findAllConfiguredOrdersByClientId(@Param("clientId") UUID clientId);

    @Query("SELECT o FROM OrderBase o WHERE o.orderCore.carId IN :carIds")
    List<OrderBase> findByCarIdIn(List<UUID> carIds);

    default void softDeleteById(UUID id) {
        findById(id).ifPresent(entity -> {
            entity.setRemoved(true);
            save(entity);
        });
    }
}
