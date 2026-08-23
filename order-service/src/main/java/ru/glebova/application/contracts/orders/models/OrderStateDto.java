package ru.glebova.application.contracts.orders.models;

public enum OrderStateDto {
    APPROVED,
    AWAITING_PAY,
    CANCELLED,
    DELIVERING,
    FINISHED,
    PAYED,
    PLACED,
    READY
}
