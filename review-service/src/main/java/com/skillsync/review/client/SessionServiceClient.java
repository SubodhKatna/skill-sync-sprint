package com.skillsync.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "session-service")
public interface SessionServiceClient {

    @GetMapping("/api/v1/sessions/{id}")
    Map<String, Object> getSessionById(@PathVariable("id") Long id);
}
