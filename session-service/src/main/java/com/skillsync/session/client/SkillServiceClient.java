package com.skillsync.session.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "skill-service")
public interface SkillServiceClient {

    @GetMapping("/skills/{id}")
    Map<String, Object> getSkillById(@PathVariable("id") Long id);
}
