package ru.glebova.presentation.mapping.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.glebova.application.contracts.testdriverequests.models.TestDriveRequestDto;
import ru.glebova.domain.testdriverequests.TestDriveRequest;

@Mapper(componentModel = "spring")
public interface TestDriveRequestMapper {
    TestDriveRequestDto toDto(TestDriveRequest request);
}
