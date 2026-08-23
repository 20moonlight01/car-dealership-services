package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.orders.models.OrderDto;
import ru.glebova.domain.orders.ConfiguredOrder;
import ru.glebova.domain.orders.OrderBase;
import ru.glebova.domain.orders.StockOrder;

@Mapper(
        componentModel = "spring",
        uses = {OrderStateMapper.class})
public interface OrderMapper {
    @Mapping(source = "state", target = "stateDto")
    @Mapping(source = "price.value", target = "price")
    OrderDto.StockOrderDto toDto(StockOrder stockOrder);

    @Mapping(source = "state", target = "stateDto")
    @Mapping(source = "price.value", target = "price")
    OrderDto.ConfiguredOrderDto toDto(ConfiguredOrder configuredOrder);

    default OrderDto toDto(OrderBase orderBase) {
        return switch (orderBase) {
            case StockOrder order -> toDto(order);
            case ConfiguredOrder order -> toDto(order);
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}
