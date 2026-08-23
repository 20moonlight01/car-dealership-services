package ru.glebova.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.glebova.application.services.CarStorageClient;
import ru.glebova.presentation.controllers.CarInfoController;
import ru.glebova.presentation.errorhandling.GlobalExceptionHandler;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CarInfoControllerTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new CarInfoController(carStorageClient))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private final CarStorageClient carStorageClient = Mockito.mock(CarStorageClient.class);

    @Test
    void getCarInfo_ServiceUnavailable_Returns503() throws Exception {
        var carId = UUID.randomUUID();

        when(carStorageClient.getCarInStock(carId.toString()))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED.withDescription("Timeout elapsed")));

        mockMvc.perform(get("/api/v1/cars/" + carId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.typeDto").value("SERVICE_UNAVAILABLE"));
    }
}
