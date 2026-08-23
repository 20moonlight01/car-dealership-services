package ru.glebova.application.services;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.glebova.grpc.CarIdRequest;
import ru.glebova.grpc.CarListResponse;
import ru.glebova.grpc.CarResponse;
import ru.glebova.grpc.EmptyRequest;

import java.util.concurrent.TimeUnit;

@Service
public class CarStorageClient {
    private static final Logger log = LoggerFactory.getLogger(CarStorageClient.class);

    @GrpcClient("storage-service")
    private Channel channel;

    public CarListResponse getCarsInStock() {
        log.info("gRPC Client: Sending request to 'GetCarsInStock'...");

        MethodDescriptor<EmptyRequest, CarListResponse> method = MethodDescriptor.<EmptyRequest, CarListResponse>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarsInStock")
                .setRequestMarshaller(ProtoUtils.marshaller(EmptyRequest.getDefaultInstance()))
                .setResponseMarshaller(ProtoUtils.marshaller(CarListResponse.getDefaultInstance()))
                .build();

        try {
            var response = ClientCalls.blockingUnaryCall(
                    channel,
                    method,
                    CallOptions.DEFAULT.withDeadlineAfter(3, TimeUnit.SECONDS),
                    EmptyRequest.getDefaultInstance());
            log.info("gRPC Client: Successfully received car list in stock");
            return response;
        } catch (StatusRuntimeException ex) {
            log.error("gRPC Client: Failed to fetch cars in stock. Status: {}, Description: {}",
                    ex.getStatus().getCode(), ex.getStatus().getDescription());
            throw ex;
        }
    }

    public CarResponse getCarInStock(String carId) {
        log.info("gRPC Client: Sending request to 'GetCarInStock'...");

        MethodDescriptor<CarIdRequest, CarResponse> method = MethodDescriptor.<CarIdRequest, CarResponse>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("ru.glebova.storage.CarStorageService/GetCarInStock")
                .setRequestMarshaller(ProtoUtils.marshaller(CarIdRequest.getDefaultInstance()))
                .setResponseMarshaller(ProtoUtils.marshaller(CarResponse.getDefaultInstance()))
                .build();

        var request = CarIdRequest.newBuilder()
                .setId(carId)
                .build();

        try {
            var response = ClientCalls.blockingUnaryCall(
                    channel,
                    method,
                    CallOptions.DEFAULT.withDeadlineAfter(3, TimeUnit.SECONDS),
                    request);
            log.info("gRPC Client: Successfully fetched car info for ID: {}", carId);
            return response;
        } catch (StatusRuntimeException ex) {
            log.error("gRPC Client: Failed to fetch car for ID: {}. Status: {}, Description: {}",
                    carId, ex.getStatus().getCode(), ex.getStatus().getDescription());
            throw ex;
        }
    }
}
