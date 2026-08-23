package ru.glebova.domain.cars;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.carparts.CarPart;

@Entity
@Table(name = "parts_models")
@SQLRestriction("removed = false")
public class PartModelCompatibility extends EntityBase {
    public CarPart getPart() {
        return part;
    }

    public void setPart(CarPart part) {
        this.part = part;
    }

    public CarModel getModel() {
        return model;
    }

    public void setModel(CarModel model) {
        this.model = model;
    }

    public PartModelCompatibility(CarPart part, CarModel model) {
        this.part = part;
        this.model = model;
    }

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private CarPart part;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    protected PartModelCompatibility() { }
}
