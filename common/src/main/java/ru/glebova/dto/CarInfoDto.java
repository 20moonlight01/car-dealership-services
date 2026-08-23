package ru.glebova.dto;

import java.util.UUID;

public record CarInfoDto(
        UUID id,
        float price)
{ }
