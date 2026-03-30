package com.skillsync.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users")
    void createProfile(@RequestBody CreateUserProfileRequest request);

    record CreateUserProfileRequest(Long userId, String name, String email) {}
}
