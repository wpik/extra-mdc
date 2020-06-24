package io.github.wpik.extramdc.aspect;

import io.github.wpik.extramdc.annotation.ExtraMdc;
import io.github.wpik.extramdc.annotation.MdcField;
import io.github.wpik.extramdc.annotation.MdcFields;
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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@SpringBootApplication
class ExtraMdcAspectTest {
    @Autowired
    private LibraryService libraryService;

    @Test
    void mdcField() {
        assertNull(MDC.get("the-isbn"));
        libraryService.book("12345", () -> {
            assertNull(MDC.get("isbn"));
            assertEquals("12345", MDC.get("the-isbn"));
        });
        assertNull(MDC.get("the-isbn"));
    }

    @Test
    void mdcFieldWithExpression() {
        assertNull(MDC.get("author"));
        assertNull(MDC.get("title"));
        assertNull(MDC.get("author-title"));
        libraryService.book(
                new Book("H. Sienkiewicz", "Quo Vadis"),
                () -> {
                    assertEquals("H. Sienkiewicz-Quo Vadis", MDC.get("author-title"));
                    assertEquals("H. Sienkiewicz", MDC.get("author"));
                    assertEquals("Quo Vadis", MDC.get("title"));
                }
        );
        assertNull(MDC.get("author"));
        assertNull(MDC.get("title"));
        assertNull(MDC.get("author-title"));
    }

    @Test
    void mdcFieldWithInvalidExpression() {
        assertNull(MDC.get("field"));
        libraryService.bookInvalidExpression(
                new Book("H. Sienkiewicz", "Quo Vadis"),
                () -> assertEquals("ExtraMdcAspectTest.Book(author=H. Sienkiewicz, title=Quo Vadis)", MDC.get("field"))
        );
        assertNull(MDC.get("field"));
    }

    @Component
    static class LibraryService {
        @ExtraMdc
        void book(
                //for @MdcField without name, warning is generated
                @MdcField @MdcField("the-isbn") String isbn, Runnable assertions) {
            assertions.run();
        }

        @ExtraMdc
        void book(
                @MdcFields({
                        @MdcField(name = "author", expression = "author"),
                        @MdcField(name = "title", expression = "title")
                })
                @MdcField(name = "author-title", expression = "author + '-' + title")
                        Book book, Runnable assertions) {
            assertions.run();
        }

        @ExtraMdc
        void bookInvalidExpression(
                @MdcField(name = "field", expression = "foo")
                        Book book, Runnable assertions) {
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
