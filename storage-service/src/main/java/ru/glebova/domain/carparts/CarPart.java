package ru.glebova.domain.carparts;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.enums.Drive;
import ru.glebova.enums.GearBox;
import ru.glebova.valueobjects.Price;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "car_parts")
@SQLRestriction("removed = false")
public abstract class CarPart extends EntityBase {
    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    @Column(nullable = false)
    protected String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "value",
                    column = @Column(name = "price", nullable = false))})
    protected Price price;

    protected CarPart() { }

    private CarPart(String name, Price price) {
        this.name = name;
        this.price = new Price(price.value());
    }

    @Entity
    @Table(name = "steering_wheels")
    @PrimaryKeyJoinColumn(name = "id")
    public static final class SteeringWheel extends CarPart {
        private SteeringWheel() { }

        public SteeringWheel(String name, Price price) {
            super(name, price);
        }
    }

    @Entity
    @Table(name = "wheels")
    @PrimaryKeyJoinColumn(name = "id")
    public static final class Wheels extends CarPart {
        private Wheels() { }

        public Wheels(String name, Price price) {
            super(name, price);
        }
    }

    @Entity
    @Table(name = "interiors")
    @PrimaryKeyJoinColumn(name = "id")
    public static final class Interior extends CarPart {
        private Interior() { }

        public Interior(String name, Price price) {
            super(name, price);
        }
    }

    @Entity
    @Table(name = "transmissions")
    @PrimaryKeyJoinColumn(name = "id")
    public static final class Transmission extends CarPart {
        public GearBox getGearBox() {
            return gearBox;
        }

        public Drive getDrive() {
            return drive;
        }

        @Enumerated(EnumType.STRING)
        @Column(name = "gear_box", nullable = false)
        private GearBox gearBox;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Drive drive;

        private Transmission() { }

        public Transmission(String name, Price price, GearBox gearBox, Drive drive) {
            super(name, price);
            this.gearBox = gearBox;
            this.drive = drive;
        }
    }
}
