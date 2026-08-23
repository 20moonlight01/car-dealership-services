package ru.glebova.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.testdriverequests.operations.PlaceTestDriveRequestCommand;
import ru.glebova.clients.StorageServiceClient;
import ru.glebova.exceptions.DomainValidationException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.exceptions.NotEnoughRightsException;
import ru.glebova.domain.testdriverequests.TestDriveRequest;
import ru.glebova.domain.testdriverequests.TestDriveRequestState;
import ru.glebova.infrastructure.persistence.repositories.TestDriveRequestJpaRepository;
import ru.glebova.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TestDriveRequestService {
    private final StorageServiceClient storageServiceClient;
    private final TestDriveRequestJpaRepository testDriveRequestRepository;

    public TestDriveRequestService(
            StorageServiceClient storageServiceClient,
            TestDriveRequestJpaRepository testDriveRequestRepository)
    {
        this.storageServiceClient = storageServiceClient;
        this.testDriveRequestRepository = testDriveRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<TestDriveRequest> getTestDriveRequestList() {
        if (SecurityUtils.currentIsAdmin() || SecurityUtils.currentIsManager()) {
            return testDriveRequestRepository.findAll();
        }

        var userId = SecurityUtils.getCurrentUserId();

        return testDriveRequestRepository.findByClientId(userId);
    }

    @Transactional(readOnly = true)
    public TestDriveRequest getTestDriveRequestInfo(UUID id) throws EntityNotFoundException {
        var request = testDriveRequestRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Test drive request with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin() && !SecurityUtils.currentIsManager()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!request.getClientId().equals(userId))
                throw new NotEnoughRightsException("User can only see their own test drive requests");
        }

        return request;
    }

    public TestDriveRequest placeTestDriveRequest(PlaceTestDriveRequestCommand command)
            throws EntityNotFoundException, DomainValidationException
    {
        var clientId = SecurityUtils.getCurrentUserId();

        if (!storageServiceClient.isTestableCar(command.carId()))
            throw new EntityNotFoundException("Testable car with such id does not exist");

        if (LocalDateTime.now().isAfter(command.time()))
            throw new DomainValidationException("Request is outdated");

        var request = new TestDriveRequest(
                clientId,
                command.carId(),
                command.time());
        request = testDriveRequestRepository.save(request);

        return request;
    }

    public TestDriveRequest cancelTestDriveRequest(UUID requestId)
            throws EntityNotFoundException
    {
        var request = testDriveRequestRepository.findById(requestId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Test drive request with such id does not exist"));

        if (!SecurityUtils.currentIsAdmin() && !SecurityUtils.currentIsManager()) {
            var userId = SecurityUtils.getCurrentUserId();
            if (!request.getClientId().equals(userId))
                throw new NotEnoughRightsException("User can only cancel their own test drive requests");
        }

        request.setState(TestDriveRequestState.CANCELLED);

        return request;
    }

    public TestDriveRequest finishTestDriveRequest(UUID requestId)
            throws EntityNotFoundException
    {
        var request = testDriveRequestRepository.findById(requestId)
                .orElseThrow(()
                        -> new EntityNotFoundException("Test drive request with such id does not exist"));

        request.setState(TestDriveRequestState.FINISHED);

        return request;
    }

    @Transactional(readOnly = true)
    public boolean isCarUsedInTestDriveRequests(UUID carId) {
        return !testDriveRequestRepository.findByCarIdIn(List.of(carId)).isEmpty();
    }
}
