package io.github.wpik.extramdc.spring;

import io.github.wpik.extramdc.aspect.ExtraMdcAspect;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class EnabledTestBase {
    @Autowired
    private ExtraMdcAspect extraMdcAspect;

    @Autowired
    private TestApplication.UserService userService;

    @Test
    void autoConfigurationHasRun() {
        assertNotNull(extraMdcAspect);
    }

    @Test
    void autoConfigurationOperational() {
        assertNull(MDC.get("username"));
        userService.login(
                new TestApplication.User("Alice"),
                () -> assertEquals("Alice", MDC.get("username"))
        );
        assertNull(MDC.get("username"));
    }
}
