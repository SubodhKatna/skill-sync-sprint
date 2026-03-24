package com.skillsync.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.service-url.defaultZone=http://localhost:8761/eureka/"
})
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
    }
}
