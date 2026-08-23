package ru.glebova.application.services;

import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glebova.application.contracts.cars.operations.GetFilteredCarsCommand;
import ru.glebova.domain.filters.CarFilter;
import ru.glebova.grpc.CarIdRequest;
import ru.glebova.grpc.CarListResponse;
import ru.glebova.grpc.CarResponse;
import ru.glebova.grpc.EmptyRequest;

import java.util.UUID;

@GrpcService
public class GrpcCarStorageService implements BindableService {
    private static final Logger log = LoggerFactory.getLogger(GrpcCarStorageService.class);

    private final CarService carService;

    public GrpcCarStorageService(CarService carService) {
        this.carService = carService;
    }

    @Override
    public ServerServiceDefinition bindService() {
        return ServerServiceDefinition.builder("ru.glebova.storage.CarStorageService")

                .addMethod(
                        MethodDescriptor.<EmptyRequest, CarListResponse>newBuilder()
                                .setType(MethodDescriptor.MethodType.UNARY)
                                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarsInStock")
                                .setRequestMarshaller(ProtoUtils.marshaller(EmptyRequest.getDefaultInstance()))
                                .setResponseMarshaller(ProtoUtils.marshaller(CarListResponse.getDefaultInstance()))
                                .build(),
                        ServerCalls.asyncUnaryCall(this::getCarsInStock)
                )

                .addMethod(
                        MethodDescriptor.<CarIdRequest, CarResponse>newBuilder()
                                .setType(MethodDescriptor.MethodType.UNARY)
                                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarInStock")
                                .setRequestMarshaller(ProtoUtils.marshaller(CarIdRequest.getDefaultInstance()))
                                .setResponseMarshaller(ProtoUtils.marshaller(CarResponse.getDefaultInstance()))
                                .build(),
                        ServerCalls.asyncUnaryCall(this::getCarInStock)
                )

                .build();
    }

    private void getCarsInStock(EmptyRequest request, StreamObserver<CarListResponse> responseObserver) {
        log.info("gRPC Server: Received 'GetCarsInStock' invocation");
        try {
            var cars = carService.getCarInStockList(new GetFilteredCarsCommand(new CarFilter[]{}));

            var listBuilder = CarListResponse.newBuilder();

            for (var car : cars) {
                var carResponse = CarResponse.newBuilder()
                        .setId(car.getId().toString())
                        .setModelId(car.getModel().getId().toString())
                        .setColor(car.getColor())
                        .setCarType(car.getCarType().name())
                        .setPrice(car.getPrice().value())
                        .addPartIds(car.getConfiguration().getSteeringWheel().getId().toString())
                        .addPartIds(car.getConfiguration().getWheels().getId().toString())
                        .addPartIds(car.getConfiguration().getInterior().getId().toString())
                        .addPartIds(car.getConfiguration().getTransmission().getId().toString())
                        .build();

                listBuilder.addCars(carResponse);
            }

            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
            log.info("gRPC Server: Successfully responded to 'GetCarsInStock'");
        } catch (Exception e) {
            log.error("gRPC Server: Error processing 'GetCarsInStock'", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private void getCarInStock(CarIdRequest request, StreamObserver<CarResponse> responseObserver) {
        log.info("gRPC Server: Received 'GetCarInStock' invocation for ID: {}", request.getId());
        try {
            var carId = UUID.fromString(request.getId());

            var car = carService.getCarInfo(carId);

            var carResponse = CarResponse.newBuilder()
                    .setId(car.getId().toString())
                    .setModelId(car.getModel().getId().toString())
                    .setColor(car.getColor())
                    .setCarType(car.getCarType().name())
                    .setPrice(car.getPrice().value())
                    .addPartIds(car.getConfiguration().getSteeringWheel().getId().toString())
                    .addPartIds(car.getConfiguration().getWheels().getId().toString())
                    .addPartIds(car.getConfiguration().getInterior().getId().toString())
                    .addPartIds(car.getConfiguration().getTransmission().getId().toString())
                    .build();

            responseObserver.onNext(carResponse);
            responseObserver.onCompleted();
            log.info("gRPC Server: Successfully responded to 'GetCarInStock' for ID: {}", request.getId());
        } catch (IllegalArgumentException e) {
            log.error("gRPC Server: Bad request. Invalid UUID format received: {}", request.getId());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("error: invalid uuid format")
                    .asRuntimeException());
        }
        catch (Exception e) {
            log.error("gRPC Server: Error processing 'GetCarInStock' for ID: {}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("error: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
