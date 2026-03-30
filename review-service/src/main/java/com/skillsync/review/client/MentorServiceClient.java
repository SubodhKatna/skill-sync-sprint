package com.skillsync.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "mentor-service")
public interface MentorServiceClient {

    @GetMapping("/mentors/{id}")
    Map<String, Object> getMentorById(@PathVariable("id") Long id);

    @PutMapping("/mentors/{id}/rating")
    void updateRating(@PathVariable("id") Long id, @RequestBody Map<String, Object> body);
}
