package com.skillsync.session.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/by-user/{userId}")
    Map<String, Object> getUserByUserId(@PathVariable("userId") Long userId);
}
