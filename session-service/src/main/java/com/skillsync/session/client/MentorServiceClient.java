package com.skillsync.session.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "mentor-service")
public interface MentorServiceClient {

    @GetMapping("/mentors/{id}")
    Map<String, Object> getMentorById(@PathVariable("id") Long id);
}
