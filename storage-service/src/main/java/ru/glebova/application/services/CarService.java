package ru.glebova.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.application.contracts.cars.operations.AddCarToStockCommand;
import ru.glebova.application.contracts.cars.operations.AddConfiguredCarCommand;
import ru.glebova.application.contracts.cars.operations.GetFilteredCarsCommand;
import ru.glebova.clients.OrderServiceClient;
import ru.glebova.domain.carparts.CarPart;
import ru.glebova.domain.carparts.CarPartsConfiguration;
import ru.glebova.domain.cars.Car;
import ru.glebova.domain.cars.CarType;
import ru.glebova.domain.cars.TestableCar;
import ru.glebova.dto.CarInfoDto;
import ru.glebova.exceptions.EntityInUseException;
import ru.glebova.exceptions.EntityNotFoundException;
import ru.glebova.exceptions.IncompatibleComponentException;
import ru.glebova.domain.filters.CarFilter;
import ru.glebova.infrastructure.persistence.repositories.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CarService {
    private final OrderServiceClient orderServiceClient;
    private final CarJpaRepository carRepository;
    private final CarModelJpaRepository carModelRepository;
    private final CarPartJpaRepository carPartRepository;
    private final PartModelJpaRepository partModelRepository;
    private final TestableCarJpaRepository testableCarRepository;

    public CarService(
            OrderServiceClient orderServiceClient,
            CarJpaRepository carRepository,
            CarModelJpaRepository carModelRepository,
            CarPartJpaRepository carPartRepository,
            PartModelJpaRepository partModelRepository,
            TestableCarJpaRepository testableCarRepository)
    {
        this.orderServiceClient = orderServiceClient;
        this.carRepository = carRepository;
        this.carModelRepository = carModelRepository;
        this.carPartRepository = carPartRepository;
        this.partModelRepository = partModelRepository;
        this.testableCarRepository = testableCarRepository;
    }

    @Transactional(readOnly = true)
    public CarInfoDto isInStock(UUID id) {
        var car = carRepository.findById(id)
                .stream()
                .filter(x -> x.getCarType() == CarType.STOCK)
                .findFirst();

        return car.map(value
                -> new CarInfoDto(value.getId(), value.getPrice().value())).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarInStockList(GetFilteredCarsCommand command) {
        List<Car> cars = carRepository.findAllCarsByType(CarType.STOCK);

        for (CarFilter filter : command.filters())
            cars = filter.Apply(cars);

        return cars;
    }

    @Transactional(readOnly = true)
    public Car getCarInfo(UUID id) throws EntityNotFoundException {
        return carRepository.findById(id)
                .stream()
                .filter(x -> x.getCarType() == CarType.STOCK)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Car with such id does not exist"));
    }

    public Car addCarToStock(AddCarToStockCommand command)
            throws EntityNotFoundException, IncompatibleComponentException
    {
        var model = carModelRepository.findById(command.modelId());
        if (model.isEmpty())
            throw new EntityNotFoundException("Model with such id does not exist");

        var newConfiguration = new CarPartsConfiguration(
                model.get().getBaseConfiguration().getSteeringWheel(),
                model.get().getBaseConfiguration().getWheels(),
                model.get().getBaseConfiguration().getInterior(),
                model.get().getBaseConfiguration().getTransmission());

        for (UUID partId : command.configuration()) {
            var part = carPartRepository.findById(partId);
            if (part.isEmpty())
                throw new EntityNotFoundException("Part with such id does not exist");

            var compatibleModels = partModelRepository.findCompatibleModelIdsByPartId(partId);
            if (!compatibleModels.contains(command.modelId()))
                throw new IncompatibleComponentException("Car part is incompatible with car model");

            newConfiguration.setPart(part.get());
        }

        var car = new Car(
                model.get(),
                command.color(),
                newConfiguration,
                CarType.STOCK);

        car = carRepository.save(car);

        return car;
    }

    public Car addConfiguredCar(AddConfiguredCarCommand command) {
        var model = carModelRepository.findById(command.modelId());
        if (model.isEmpty())
            throw new EntityNotFoundException("Car model with such id does not exist");

        var parts = carPartRepository.findAllById(Arrays.asList(command.newPartIds()));
        if (Arrays.stream(command.newPartIds()).distinct().count() != parts.size())
            throw new EntityNotFoundException("Some car part ids are not valid");
        for (CarPart part : parts)
            if (!partModelRepository.findCompatibleModelIdsByPartId(part.getId())
                    .contains(command.modelId()))
                throw new IncompatibleComponentException("Car part is incompatible with car model");

        var carConfigurator = new Car.Builder();
        carConfigurator.setModel(model.get());
        for (CarPart part : parts)
            carConfigurator.setPart(part);
        carConfigurator.setColor(command.color());
        carConfigurator.setType(CarType.CONFIGURED);
        var car = carConfigurator.build();
        car = carRepository.save(car);

        return car;
    }

    public Car removeCarFromStock(UUID id)
            throws EntityNotFoundException, EntityInUseException {
        var car = carRepository.findById(id)
                .stream()
                .filter(x -> x.getCarType() == CarType.STOCK)
                .findFirst();
        if (car.isEmpty())
            throw new EntityNotFoundException("Car with such id does not exist");

        if (orderServiceClient.isCarUsedInOrders(id) || orderServiceClient.isCarUsedInTestDriveRequests(id))
            throw new EntityInUseException("Car is used in orders");

        var testable = testableCarRepository.findByCarId(id);
        if (testable.isPresent())
            testableCarRepository.softDeleteByCarId(id);

        carRepository.softDeleteById(id);

        return car.get();
    }

    @Transactional(readOnly = true)
    public boolean isTestable(UUID id) {
        return testableCarRepository.findByCarId(id).isPresent();
    }

    @Transactional(readOnly = true)
    public List<Car> getTestableCarList(GetFilteredCarsCommand command) {
        List<Car> cars = testableCarRepository.findAll()
                .stream()
                .map(TestableCar::getCar)
                .toList();

        for (CarFilter filter : command.filters())
            cars = filter.Apply(cars);

        return cars;
    }

    public Car addTestableCar(UUID id) throws EntityNotFoundException
    {
        var car = carRepository.findById(id)
                .stream()
                .filter(x -> x.getCarType() == CarType.STOCK)
                .findFirst();
        if (car.isEmpty())
            throw new EntityNotFoundException("Car with such id does not exist");

        var testableCar = new TestableCar(car.get());

        testableCarRepository.save(testableCar);

        return car.get();
    }

    public Car removeTestableCar(UUID id) throws EntityNotFoundException {
        var car = testableCarRepository.findByCarId(id);
        if (car.isEmpty())
            throw new EntityNotFoundException("Car with such id is not available for test drive");

        if (orderServiceClient.isCarUsedInTestDriveRequests(id))
            throw new EntityInUseException("Car is used in orders");

        testableCarRepository.softDeleteByCarId(id);

        return car.get().getCar();
    }
}
