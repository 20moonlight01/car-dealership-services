package ru.glebova.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.glebova.dto.CarInfoDto;
import ru.glebova.requests.AddConfiguredCarRequest;

import java.util.UUID;

@Component
public class StorageServiceClient {
    private final RestTemplate restTemplate;
    private final String storageServiceUrl;

    public StorageServiceClient(
            RestTemplate restTemplate,
            @Value("${storage-service.url:http://localhost:8081}") String storageServiceUrl)
    {
        this.restTemplate = restTemplate;
        this.storageServiceUrl = storageServiceUrl;
    }

    public CarInfoDto isExistingStockCar(UUID carId) {
        String url = storageServiceUrl + "/api/cars/" + carId + "/in-stock";
        try {
            return restTemplate.getForObject(url, CarInfoDto.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public CarInfoDto addConfiguredCar(
            UUID modelId,
            UUID[] newPartIds,
            String color)
    {
        var request = new AddConfiguredCarRequest(modelId, newPartIds, color);
        String url = storageServiceUrl + "/api/cars/configured";

        try {
            return restTemplate.postForObject(url, request, CarInfoDto.class);
        } catch (HttpClientErrorException e) {
            return null;
        }
    }

    public boolean isTestableCar(UUID carId) {
        String url = storageServiceUrl + "/api/cars/" + carId + "/is-testable";
        return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
    }
}
