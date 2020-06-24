package io.github.wpik.extramdc.spring;

import io.github.wpik.extramdc.annotation.ExtraMdc;
import io.github.wpik.extramdc.annotation.MdcField;
import io.github.wpik.extramdc.aspect.ExtraMdcAspect;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@SpringBootApplication
class ExtraMdcAutoConfigurationTest {
    @Autowired
    private ExtraMdcAspect extraMdcAspect;

    @Autowired
    private UserService userService;

    @Test
    void autoConfigurationHasRun() {
        assertNotNull(extraMdcAspect);
    }

    @Test
    void autoConfigurationOperational() {
        assertNull(MDC.get("username"));
        userService.login(
                new User("Alice"),
                () -> assertEquals("Alice", MDC.get("username"))
        );
        assertNull(MDC.get("username"));
    }

    @Component
    static class UserService {
        @ExtraMdc
        void login(@MdcField(name = "username", expression = "name") User user, Runnable assertions) {
            assertions.run();
        }
    }

    @Value
    static class User {
        private final String name;
    }

    @EnableAspectJAutoProxy
    @TestConfiguration
    static class TestConfig {
    }
}
