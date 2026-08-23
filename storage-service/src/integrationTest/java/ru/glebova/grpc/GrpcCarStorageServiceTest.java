package ru.glebova.grpc;

import io.grpc.*;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.glebova.application.services.CarService;
import ru.glebova.application.services.GrpcCarStorageService;
import ru.glebova.domain.carparts.CarPart;
import ru.glebova.domain.carparts.CarPartsConfiguration;
import ru.glebova.domain.carparts.Engine;
import ru.glebova.domain.cars.Car;
import ru.glebova.domain.cars.CarModel;
import ru.glebova.domain.cars.CarType;
import ru.glebova.enums.CarBody;
import ru.glebova.enums.Drive;
import ru.glebova.enums.Fuel;
import ru.glebova.enums.GearBox;
import ru.glebova.valueobjects.Power;
import ru.glebova.valueobjects.Price;
import ru.glebova.valueobjects.Volume;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {GrpcCarStorageServiceTest.class})
public class GrpcCarStorageServiceTest {
    @MockitoBean
    private CarService carService;

    private Server server;
    private ManagedChannel channel;

    @BeforeEach
    void setUp() throws Exception {
        var serverName = InProcessServerBuilder.generateName();

        var grpcServer = new GrpcCarStorageService(carService);
        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(grpcServer.bindService())
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void getCarInStock_Success() {
        var mockCar = createMockCar();
        var carId = mockCar.getId();

        when(carService.getCarInfo(carId)).thenReturn(mockCar);

        MethodDescriptor<CarIdRequest, CarResponse> method = MethodDescriptor.<CarIdRequest, CarResponse>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarInStock")
                .setRequestMarshaller(ProtoUtils.marshaller(CarIdRequest.getDefaultInstance()))
                .setResponseMarshaller(ProtoUtils.marshaller(CarResponse.getDefaultInstance()))
                .build();

        CarIdRequest request = CarIdRequest.newBuilder().setId(carId.toString()).build();

        CarResponse response = ClientCalls.blockingUnaryCall(channel, method, io.grpc.CallOptions.DEFAULT, request);
        assertNotNull(response);
    }

    @Test
    void getCarInStock_InvalidArgument_ThrowsException() {
        MethodDescriptor<CarIdRequest, CarResponse> method = MethodDescriptor.<CarIdRequest, CarResponse>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarInStock")
                .setRequestMarshaller(ProtoUtils.marshaller(CarIdRequest.getDefaultInstance()))
                .setResponseMarshaller(ProtoUtils.marshaller(CarResponse.getDefaultInstance()))
                .build();

        CarIdRequest request = CarIdRequest.newBuilder().setId("invalid-uuid").build();

        var exception = assertThrows(StatusRuntimeException.class, () -> {
            ClientCalls.blockingUnaryCall(channel, method, io.grpc.CallOptions.DEFAULT, request);
        });

        assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode());
    }

    private CarPart.SteeringWheel createMockSteeringWheel() {
        var part = new CarPart.SteeringWheel(
                "steering wheel",
                new Price(100));
        part.setId(UUID.randomUUID());
        return part;
    }

    private CarPart.Wheels createMockWheels() {
        var part = new CarPart.Wheels(
                "wheels",
                new Price(100));
        part.setId(UUID.randomUUID());
        return part;
    }

    private CarPart.Interior createMockInterior() {
        var part = new CarPart.Interior(
                "interior",
                new Price(100));
        part.setId(UUID.randomUUID());
        return part;
    }

    private CarPart.Transmission createMockTransmission() {
        var part = new CarPart.Transmission(
                "transmission",
                new Price(100),
                GearBox.AUTOMATIC,
                Drive.ALL);
        part.setId(UUID.randomUUID());
        return part;
    }

    private CarPartsConfiguration createMockCarConfiguration() {
        return new CarPartsConfiguration(
                createMockSteeringWheel(),
                createMockWheels(),
                createMockInterior(),
                createMockTransmission());
    }

    private CarModel createMockCarModel() {
        var model = new CarModel(
                "car model",
                "brand",
                new Price(500),
                CarBody.SEDAN,
                new Engine(
                        Fuel.PETROL,
                        new Power(50),
                        new Volume(50)
                ),
                createMockCarConfiguration());
        model.setId(UUID.randomUUID());
        return model;
    }

    private Car createMockCar() {
        var builder = new Car.Builder();
        var car = builder.setModel(createMockCarModel())
                .setType(CarType.STOCK)
                .setColor("red")
                .build();
        car.setId(UUID.randomUUID());
        return car;
    }
}
