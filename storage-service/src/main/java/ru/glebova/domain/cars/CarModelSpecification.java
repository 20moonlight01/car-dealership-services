package ru.glebova.domain.cars;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CarModelSpecification {
    public static Specification<CarModel> filterByBrandName(String brandName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("brandName"), brandName);
    }

    public static Specification<CarModel> filterBySteeringWheel(UUID steeringWheelId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("baseConfiguration")
                                .get("steeringWheel")
                                .get("id"),
                        steeringWheelId);
    }

    public static Specification<CarModel> filterByWheels(UUID wheelsId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("baseConfiguration")
                                .get("wheels")
                                .get("id"),
                        wheelsId);
    }

    public static Specification<CarModel> filterByInterior(UUID interiorId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("baseConfiguration")
                                .get("interior")
                                .get("id"),
                        interiorId);
    }

    public static Specification<CarModel> filterByTransmission(UUID transmissionId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("baseConfiguration")
                                .get("transmission")
                                .get("id"),
                        transmissionId);
    }
}
