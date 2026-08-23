package ru.glebova.presentation.errorhandling;

import ru.glebova.presentation.errorhandling.ErrorTypeDto;

public record ErrorDto(ErrorTypeDto typeDto, String message) { }
