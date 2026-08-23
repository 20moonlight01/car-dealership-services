package ru.glebova.domain.cars;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLRestriction;
import ru.glebova.domain.EntityBase;
import ru.glebova.domain.carparts.CarPartsConfiguration;
import ru.glebova.domain.carparts.Engine;
import ru.glebova.enums.CarBody;
import ru.glebova.valueobjects.Price;

@Entity
@Table(name = "car_models")
@SQLRestriction("removed = false")
public class CarModel extends EntityBase {
    protected CarModel() { }

    public CarModel(
            String name,
            String brandName,
            Price standardPrice,
            CarBody body,
            Engine engine,
            CarPartsConfiguration baseConfiguration)
    {
        this.name = name;
        this.brandName = brandName;
        this.standardPrice = standardPrice;
        this.body = body;
        this.engine = engine;
        this.baseConfiguration = baseConfiguration;
    }

    public String getName() {
        return name;
    }

    public String getBrandName() {
        return brandName;
    }

    public Price getStandardPrice() {
        return standardPrice;
    }

    public CarBody getBody() {
        return body;
    }

    public Engine getEngine() {
        return engine;
    }

    public CarPartsConfiguration getBaseConfiguration() {
        return baseConfiguration;
    }

    @Column(nullable = false)
    private String name;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "value",
                    column = @Column(name = "standard_price", nullable = false))})
    private Price standardPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarBody body;

    @Embedded
    private Engine engine;

    @Embedded
    private CarPartsConfiguration baseConfiguration;
}
