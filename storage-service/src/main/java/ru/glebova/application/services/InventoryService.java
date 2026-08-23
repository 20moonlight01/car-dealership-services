package ru.glebova.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.domain.carparts.StockCarPart;
import ru.glebova.exceptions.DomainValidationException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.infrastructure.persistence.repositories.CarPartJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.StockCarPartJpaRepository;

import java.util.UUID;

@Service
@Transactional
public class InventoryService {
    private final CarPartJpaRepository carPartRepository;
    private final StockCarPartJpaRepository stockCarPartRepository;

    public InventoryService(
            CarPartJpaRepository carPartRepository,
            StockCarPartJpaRepository stockCarPartRepository)
    {
        this.carPartRepository = carPartRepository;
        this.stockCarPartRepository = stockCarPartRepository;
    }

    public boolean carPartIsAvailable(UUID carPartId, int quantity) {
        var stockCarPart = getStockCarPart(carPartId);

        return stockCarPart.getQuantity() - stockCarPart.getReserved() >= quantity;
    }

    public StockCarPart getStockCarPart(UUID carPartId) {
        var carPart = carPartRepository.findById(carPartId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Car part with such id does not exist"));

        return stockCarPartRepository.findByCarPartId(carPartId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Car part with such id does not exist"));
    }

    public StockCarPart replenishCarPart(UUID carPartId, int quantity) {
        var stockCarPart = getStockCarPart(carPartId);

        stockCarPart.setQuantity(stockCarPart.getQuantity() + quantity);

        return stockCarPart;
    }

    public StockCarPart reserveCarPart(UUID carPartId, int quantity) {
        var stockCarPart = getStockCarPart(carPartId);

        if (!carPartIsAvailable(carPartId, quantity))
            throw new DomainValidationException("Not enough unreserved car parts in stock");

        stockCarPart.setReserved(stockCarPart.getReserved() + quantity);

        return stockCarPart;
    }

    public StockCarPart unreserveCarPart(UUID carPartId, int quantity) {
        var stockCarPart = getStockCarPart(carPartId);

        if (stockCarPart.getReserved() < quantity)
            throw new DomainValidationException("Not enough reserved car parts in stock");

        stockCarPart.setReserved(stockCarPart.getReserved() - quantity);

        return stockCarPart;
    }

    public StockCarPart useCarPart(UUID carPartId, int quantity) {
        var stockCarPart = getStockCarPart(carPartId);

        if (stockCarPart.getQuantity() < quantity)
            throw new DomainValidationException("Not enough car parts in stock");

        stockCarPart.setQuantity(stockCarPart.getQuantity() - quantity);

        return stockCarPart;
    }
}
