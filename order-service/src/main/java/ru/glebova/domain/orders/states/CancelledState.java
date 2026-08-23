package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CANCELLED")
public class CancelledState extends OrderStateBase { }
