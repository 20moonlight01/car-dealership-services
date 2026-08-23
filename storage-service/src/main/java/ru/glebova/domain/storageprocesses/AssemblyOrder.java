package ru.glebova.domain.storageprocesses;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.cars.Car;

import java.util.UUID;

@Entity
@Table(name = "assembly_orders")
@SQLRestriction("removed = false")
public class AssemblyOrder extends EntityBase {
    @Column(name = "source_order_id", nullable = false)
    private UUID sourceOrderId;

    @Column(name = "source_order_type", nullable = false)
    private String sourceOrderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private AssemblyOrderState state;

    @Column(name = "warehouse_admin_id", nullable = false)
    private UUID warehouseAdminId;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "trace_id", nullable = false)
    private String traceId;

    protected AssemblyOrder() { }

    public AssemblyOrder(
            UUID sourceOrderId,
            String sourceOrderType,
            AssemblyOrderState state,
            UUID warehouseAdminId,
            Car car,
            String traceId)
    {
        this.sourceOrderId = sourceOrderId;
        this.sourceOrderType = sourceOrderType;
        this.state = state;
        this.warehouseAdminId = warehouseAdminId;
        this.car = car;
        this.traceId = traceId;
    }

    public UUID getSourceOrderId() {
        return sourceOrderId;
    }

    public void setSourceOrderId(UUID sourceOrderId) {
        this.sourceOrderId = sourceOrderId;
    }

    public String getSourceOrderType() {
        return sourceOrderType;
    }

    public void setSourceOrderType(String sourceOrderType) {
        this.sourceOrderType = sourceOrderType;
    }

    public AssemblyOrderState getState() {
        return state;
    }

    public void setState(AssemblyOrderState state) {
        this.state = state;
    }

    public UUID getWarehouseAdminId() {
        return warehouseAdminId;
    }

    public void setWarehouseAdminId(UUID warehouseAdminId) {
        this.warehouseAdminId = warehouseAdminId;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
