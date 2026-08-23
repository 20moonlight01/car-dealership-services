package ru.glebova.application.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.cars.operations.AddCarModelCommand;
import ru.glebova.application.contracts.cars.operations.AddCarPartCommand;
import ru.glebova.application.contracts.cars.operations.GetFilteredCarModelsCommand;
import ru.glebova.domain.carparts.CarPart;
import ru.glebova.domain.carparts.CarPartsConfiguration;
import ru.glebova.domain.carparts.Engine;
import ru.glebova.domain.cars.CarModel;
import ru.glebova.domain.cars.CarModelSpecification;
import ru.glebova.domain.cars.PartModelCompatibility;
import ru.glebova.exceptions.DomainValidationException;
import ru.glebova.exceptions.EntityAlreadyExistsException;
import ru.glebova.exceptions.EntityInUseException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.valueobjects.Power;
import ru.glebova.valueobjects.Price;
import ru.glebova.valueobjects.Volume;
import ru.glebova.infrastructure.persistence.repositories.CarJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.CarModelJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.CarPartJpaRepository;
import ru.glebova.infrastructure.persistence.repositories.PartModelJpaRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CarDetailsService {
    private final CarModelJpaRepository carModelRepository;
    private final CarPartJpaRepository carPartRepository;
    private final PartModelJpaRepository partModelRepository;
    private final CarJpaRepository carRepository;

    public CarDetailsService(
            CarModelJpaRepository carModelRepository,
            CarPartJpaRepository carPartRepository,
            PartModelJpaRepository partModelRepository,
            CarJpaRepository carRepository)
    {
        this.carModelRepository = carModelRepository;
        this.carPartRepository = carPartRepository;
        this.partModelRepository = partModelRepository;
        this.carRepository = carRepository;
    }

    @Transactional(readOnly = true)
    public List<CarModel> getCarModelList(GetFilteredCarModelsCommand command) {
        Specification<CarModel> specification = Specification.allOf();

        if (command.brandName() != null)
            specification = specification.and(CarModelSpecification.filterByBrandName(command.brandName()));
        if (command.steeringWheelId() != null)
            specification = specification.and(CarModelSpecification.filterBySteeringWheel(command.steeringWheelId()));
        if (command.wheelsId() != null)
            specification = specification.and(CarModelSpecification.filterByWheels(command.wheelsId()));
        if (command.interiorId() != null)
            specification = specification.and(CarModelSpecification.filterByInterior(command.interiorId()));
        if (command.transmissionId() != null)
            specification = specification.and(CarModelSpecification.filterByTransmission(command.transmissionId()));

        return carModelRepository.findAll(specification);
    }

    @Transactional(readOnly = true)
    public CarModel getCarModelInfo(UUID id) throws EntityNotFoundException {
        return carModelRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Car model with such id does not exist"));
    }

    public CarModel addCarModel(AddCarModelCommand command)
            throws EntityAlreadyExistsException, EntityNotFoundException, DomainValidationException
    {
        var models = carModelRepository.findByBrandNameInAndNameIn(List.of(command.brandName()), List.of(command.name()));
        if (!models.isEmpty())
            throw new EntityAlreadyExistsException("Car model with such name already exists");

        var parts = new ArrayList<CarPart>();
        var configurationBuilder = new CarPartsConfiguration.Builder();

        for (UUID partId : command.baseConfiguration()) {
            var part = carPartRepository.findById(partId);
            if (part.isEmpty())
                throw new EntityNotFoundException("Car part with such id does not exist");

            parts.add(part.get());
            configurationBuilder.setPart(part.get());
        }

        CarPartsConfiguration configuration;

        try {
            configuration = configurationBuilder.Build();
        }
        catch (RuntimeException e) {
            throw new DomainValidationException("Configuration is not complete");
        }

        var model = new CarModel(
                command.name(),
                command.brandName(),
                new Price(command.standardPrice()),
                command.body(),
                new Engine(
                        command.fuel(),
                        new Power(command.power()),
                        new Volume(command.volume())),
                configuration);
        var addedModel = carModelRepository.save(model);

        for (CarPart part : parts) {
            partModelRepository.save(
                    new PartModelCompatibility(part, addedModel));
        }

        return addedModel;
    }

    public CarModel removeCarModel(UUID id)
            throws EntityNotFoundException, EntityInUseException
    {
        var model = carModelRepository.findById(id);
        if (model.isEmpty())
            throw new EntityNotFoundException("Car model with such id does not exist");

        var cars = carRepository.findByModelIdIn(List.of(id));
        if (!cars.isEmpty())
            throw new EntityInUseException("Model is used in cars");

        partModelRepository.softDeleteAllByModelId(model.get().getId());
        carModelRepository.softDeleteById(id);

        return model.get();
    }

    @Transactional(readOnly = true)
    public List<CarPart> getCarPartList() {
        return carPartRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CarPart getCarPartInfo(UUID id) throws EntityNotFoundException {
        return carPartRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Car part with such id does not exist"));
    }

    public CarPart AddSteeringWheel(AddCarPartCommand.AddSteeringWheelCommand command)
            throws EntityAlreadyExistsException, EntityNotFoundException
    {
        var parts = carPartRepository.findByNameIn(List.of(command.getName()));
        if (!parts.isEmpty())
            throw new EntityAlreadyExistsException("Car part with such name already exists");

        var models = carModelRepository.findAllById(Arrays.stream(command.getModelIds()).toList());
        if (Arrays.stream(command.getModelIds()).distinct().count() != models.size())
            throw new EntityNotFoundException("Some car model ids are not valid");

        var part = new CarPart.SteeringWheel(
                command.getName(),
                new Price(command.getPrice()));

        part = carPartRepository.save(part);
        for (CarModel model : models)
            partModelRepository.save(
                    new PartModelCompatibility(part, model));

        return part;
    }

    public CarPart AddWheels(AddCarPartCommand.AddWheelsCommand command)
            throws EntityAlreadyExistsException, EntityNotFoundException
    {
        var parts = carPartRepository.findByNameIn(List.of(command.getName()));
        if (!parts.isEmpty())
            throw new EntityAlreadyExistsException("Car part with such name already exists");

        var models = carModelRepository.findAllById(Arrays.stream(command.getModelIds()).toList());
        if (Arrays.stream(command.getModelIds()).distinct().count() != models.size())
            throw new EntityNotFoundException("Some car model ids are not valid");

        var part = new CarPart.Wheels(
                command.getName(),
                new Price(command.getPrice()));

        part = carPartRepository.save(part);
        for (CarModel model : models)
            partModelRepository.save(
                    new PartModelCompatibility(part, model));

        return part;
    }

    public CarPart AddInterior(AddCarPartCommand.AddInteriorCommand command)
            throws EntityAlreadyExistsException, EntityNotFoundException
    {
        var parts = carPartRepository.findByNameIn(List.of(command.getName()));
        if (!parts.isEmpty())
            throw new EntityAlreadyExistsException("Car part with such name already exists");

        var models = carModelRepository.findAllById(Arrays.stream(command.getModelIds()).toList());
        if (Arrays.stream(command.getModelIds()).distinct().count() != models.size())
            throw new EntityNotFoundException("Some car model ids are not valid");

        var part = new CarPart.Interior(
                command.getName(),
                new Price(command.getPrice()));

        part = carPartRepository.save(part);
        for (CarModel model : models)
            partModelRepository.save(
                    new PartModelCompatibility(part, model));

        return part;
    }

    public CarPart AddTransmission(AddCarPartCommand.AddTransmissionCommand command)
            throws EntityAlreadyExistsException, EntityNotFoundException
    {
        var parts = carPartRepository.findByNameIn(List.of(command.getName()));
        if (!parts.isEmpty())
            throw new EntityAlreadyExistsException("Car part with such name already exists");

        var models = carModelRepository.findAllById(Arrays.stream(command.getModelIds()).toList());
        if (Arrays.stream(command.getModelIds()).distinct().count() != models.size())
            throw new EntityNotFoundException("Some car model ids are not valid");

        var part = new CarPart.Transmission(
                command.getName(),
                new Price(command.getPrice()),
                command.getGearBox(),
                command.getDrive());

        part = carPartRepository.save(part);
        for (CarModel model : models)
            partModelRepository.save(
                    new PartModelCompatibility(part, model));

        return part;
    }

    public CarPart removeCarPart(UUID id)
            throws EntityNotFoundException, EntityInUseException
    {
        var part = carPartRepository.findById(id);
        if (part.isEmpty())
            throw new EntityNotFoundException("Car part with such id does not exist");

        var models = partModelRepository.findCompatibleModelIdsByPartId(id);
        if (!models.isEmpty())
            throw new EntityInUseException("Car part is used in models");

        var cars = carRepository.findAll()
                .stream()
                .filter(x -> x.getConfiguration().getSteeringWheel().getId().equals(id)
                        || x.getConfiguration().getWheels().getId().equals(id)
                        || x.getConfiguration().getInterior().getId().equals(id)
                        || x.getConfiguration().getTransmission().getId().equals(id))
                .toList();
        if (!cars.isEmpty())
            throw new EntityInUseException("Car part is used in cars");

        carPartRepository.softDeleteById(id);

        return part.get();
    }
}
