package ru.glebova.domain.testdriverequests;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "test_drive_requests")
@SQLRestriction("removed = false")
public class TestDriveRequest extends EntityBase {
    public UUID getClientId() {
        return clientId;
    }

    public UUID getCarId() {
        return carId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public TestDriveRequestState getState() {
        return state;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestDriveRequestState state;

    protected TestDriveRequest() { }

    public TestDriveRequest(
            UUID clientId,
            UUID carId,
            LocalDateTime time)
    {
        this.clientId = clientId;
        this.carId = carId;
        this.time = time;
        this.state = TestDriveRequestState.PENDING;
    }

    public void setState(TestDriveRequestState state) {
        if (this.state != TestDriveRequestState.PENDING)
            return;

        this.state = state;
    }
}
