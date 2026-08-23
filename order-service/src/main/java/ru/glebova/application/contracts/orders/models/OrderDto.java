package ru.glebova.application.contracts.orders.models;

import java.util.UUID;

public abstract class OrderDto {
    public UUID getId() {
        return id;
    }

    public OrderStateDto getStateDto() {
        return stateDto;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public UUID getCarId() {
        return carId;
    }

    public float getPrice() {
        return price;
    }

    protected final UUID id;
    protected final OrderStateDto stateDto;
    protected final UUID clientId;
    protected final UUID managerId;
    protected final UUID carId;
    protected final float price;

    protected OrderDto(
            UUID id,
            OrderStateDto stateDto,
            UUID clientId,
            UUID managerId,
            UUID carId,
            float price)
    {
        this.id = id;
        this.stateDto = stateDto;
        this.clientId = clientId;
        this.managerId = managerId;
        this.carId = carId;
        this.price = price;
    }

    public static final class StockOrderDto extends OrderDto {
        public StockOrderDto(
                UUID id,
                OrderStateDto stateDto,
                UUID clientId,
                UUID managerId,
                UUID carId,
                float price)
        {
            super(id, stateDto, clientId, managerId, carId, price);
        }
    }

    public static final class ConfiguredOrderDto extends OrderDto {
        public ConfiguredOrderDto(
                UUID id,
                OrderStateDto stateDto,
                UUID clientId,
                UUID managerId,
                UUID carId,
                float price)
        {
            super(id, stateDto, clientId, managerId, carId, price);
        }
    }
}
