package ru.glebova.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class OrderServiceClient {
    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    public OrderServiceClient(
            RestTemplate restTemplate,
            @Value("${order-service.url:http://localhost:8080}") String orderServiceUrl)
    {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    public boolean isCarUsedInOrders(UUID carId) {
        String url = orderServiceUrl + "/api/orders/by-car/" + carId + "/exists";
        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }

    public boolean isCarUsedInTestDriveRequests(UUID carId) {
        String url = orderServiceUrl + "/api/test-drive-requests/by-car/" + carId + "/exists";
        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }
}
