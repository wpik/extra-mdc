package com.github.wpik.extramdc.aspect;

import com.github.wpik.extramdc.annotation.ExtraMdc;
import com.github.wpik.extramdc.annotation.MdcField;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@SpringBootApplication
class ExtraMdcAspectTest {
    @Autowired
    private LibraryService libraryService;

    @Test
    void mdcField() {
        libraryService.book("12345", () -> assertEquals("12345", MDC.get("the-isbn")));
    }

    @Test
    void mdcFieldWithExpression() {
        libraryService.book(
                new Book("H. Sienkiewicz", "Quo Vadis"),
                () -> assertEquals("H. Sienkiewicz-Quo Vadis", MDC.get("author-title"))
        );
    }

    @Component
    static class LibraryService {
        @ExtraMdc
        void book(@MdcField(name = "the-isbn") String isbn, Runnable assertions) {
            assertions.run();
        }

        @ExtraMdc
        void book(@MdcField(name = "author-title", expression = "author + '-' + title") Book book, Runnable assertions) {
            assertions.run();
        }
    }

    @Value
    static class Book {
        private final String author;
        private final String title;
    }

    @EnableAspectJAutoProxy
    @TestConfiguration
    static class TestConfig {
        @Bean
        ExtraMdcAspect extraMdcAspect() {
            return new ExtraMdcAspect();
        }
    }
}
