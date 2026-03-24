package com.skillsync.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "eureka.instance.hostname=localhost",
        "spring.cloud.inetutils.default-hostname=localhost",
        "spring.cloud.inetutils.default-ip-address=127.0.0.1"
})
class DiscoveryServerApplicationTest {

    @Test
    void contextLoads() {
    }
}
