package com.skillsync.session.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String authHeader = attrs.getRequest().getHeader("Authorization");
                if (authHeader != null) {
                    request.getHeaders().set("Authorization", authHeader);
                }
            }
            return execution.execute(request, body);
        };
        restTemplate.setInterceptors(List.of(authInterceptor));
        return restTemplate;
    }
}
