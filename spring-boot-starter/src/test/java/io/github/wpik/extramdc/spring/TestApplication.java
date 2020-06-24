package io.github.wpik.extramdc.spring;

import io.github.wpik.extramdc.annotation.ExtraMdc;
import io.github.wpik.extramdc.annotation.MdcField;
import lombok.Value;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class TestApplication {

    @Component
    public static class UserService {
        @ExtraMdc
        void login(@MdcField(name = "username", expression = "name") User user, Runnable assertions) {
            System.out.println("username=" + MDC.get("username"));
            assertions.run();
        }
    }

    @Value
    public static class User {
        private final String name;
    }
}
