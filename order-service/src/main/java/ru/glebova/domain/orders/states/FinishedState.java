package ru.glebova.domain.orders.states;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FINISHED")
public class FinishedState extends OrderStateBase { }
