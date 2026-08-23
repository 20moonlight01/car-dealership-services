package ru.glebova.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.application.contracts.testdriverequests.models.TestDriveRequestDto;
import ru.glebova.application.services.TestDriveRequestService;
import ru.glebova.presentation.mapping.commands.PlaceTestDriveRequestMapper;
import ru.glebova.presentation.mapping.dto.TestDriveRequestMapper;
import ru.glebova.presentation.requests.PlaceTestDriveRequestRequest;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-drive-requests")
@Tag(name = "Test-Drive Requests", description = "Operations with test-drive requests")
public class TestDriveRequestController {
    private final TestDriveRequestService testDriveRequestService;
    private final PlaceTestDriveRequestMapper placeTestDriveRequestMapper;
    private final TestDriveRequestMapper testDriveRequestMapper;

    public TestDriveRequestController(
            TestDriveRequestService testDriveRequestService,
            PlaceTestDriveRequestMapper placeTestDriveRequestMapper,
            TestDriveRequestMapper testDriveRequestMapper)
    {
        this.testDriveRequestService = testDriveRequestService;
        this.placeTestDriveRequestMapper = placeTestDriveRequestMapper;
        this.testDriveRequestMapper = testDriveRequestMapper;
    }

    @GetMapping("/all")
    @Operation(summary = "Get information about all test-drive requests")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public List<TestDriveRequestDto> getTestDriveRequestList() {
        return testDriveRequestService.getTestDriveRequestList()
                .stream()
                .map(testDriveRequestMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about particular test-drive request")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TestDriveRequestDto getTestDriveRequestInfo(@PathVariable UUID id) {
        return testDriveRequestMapper.toDto(
                testDriveRequestService.getTestDriveRequestInfo(id));
    }

    @PostMapping
    @Operation(summary = "Place test-drive request for particular testable car")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public TestDriveRequestDto placeTestDriveRequest(@Valid @RequestBody PlaceTestDriveRequestRequest request) {
        return testDriveRequestMapper.toDto(
                testDriveRequestService.placeTestDriveRequest(placeTestDriveRequestMapper.toCommand(request)));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel particular test-drive request")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
    public TestDriveRequestDto tryCancelTestDriveRequest(@PathVariable UUID id) {
        return testDriveRequestMapper.toDto(
                testDriveRequestService.cancelTestDriveRequest(id));
    }

    @PatchMapping("/{id}/finish")
    @Operation(summary = "Finish particular test-drive request")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public TestDriveRequestDto tryFinishTestDriveRequest(@PathVariable UUID id) {
        return testDriveRequestMapper.toDto(
                testDriveRequestService.finishTestDriveRequest(id));
    }

    @GetMapping("/by-car/{id}/exists")
    @Operation(summary = "")
    public boolean isCarUsedInTestDriveRequests(@PathVariable UUID id) {
        return testDriveRequestService.isCarUsedInTestDriveRequests(id);
    }
}
