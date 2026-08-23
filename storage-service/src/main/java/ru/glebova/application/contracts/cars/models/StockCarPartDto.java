package ru.glebova.application.contracts.cars.models;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import ru.glebova.domain.carparts.CarPart;

import java.util.UUID;

public record StockCarPartDto(
        UUID id,
        UUID carPartId,
        int quantity,
        int reserved)
{ }
