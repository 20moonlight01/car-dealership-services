package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import ru.glebova.application.contracts.orders.models.OrderStateDto;
import ru.glebova.domain.orders.states.*;

@Mapper(componentModel = "spring")
public abstract class OrderStateMapper {
    public OrderStateDto toDto(OrderStateBase state) {
        return switch (state) {
            case ApprovedState _ -> OrderStateDto.APPROVED;
            case AwaitingPayState _ -> OrderStateDto.AWAITING_PAY;
            case CancelledState _ -> OrderStateDto.CANCELLED;
            case DeliveringState _ -> OrderStateDto.DELIVERING;
            case FinishedState _ -> OrderStateDto.FINISHED;
            case PayedState _ -> OrderStateDto.PAYED;
            case PlacedState _ -> OrderStateDto.PLACED;
            case ReadyState _ -> OrderStateDto.READY;
            default -> throw new IllegalArgumentException("Unknown state");
        };
    }
}
