package com.skillsync.auth.client;

import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.exception.ConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://user-service").build();
    }

    public void createProfile(User user) {
        try {
            restClient.post()
                    .uri("/users")
                    .body(new CreateUserProfileRequest(user.getId(), user.getName(), user.getEmail()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new ConflictException("User profile already exists for user: " + user.getId());
            }
            throw new BadRequestException("User profile creation failed: " + ex.getResponseBodyAsString());
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("User service is unavailable", ex);
        }
    }

    private record CreateUserProfileRequest(Long userId, String name, String email) {
    }
}
