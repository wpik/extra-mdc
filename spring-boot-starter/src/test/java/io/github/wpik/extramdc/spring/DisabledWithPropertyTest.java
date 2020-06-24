package io.github.wpik.extramdc.spring;

import io.github.wpik.extramdc.aspect.ExtraMdcAspect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(properties = {"extra-mdc.enabled=false"})
public class DisabledWithPropertyTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void extraMdcBeanShouldNotBeRegistered() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(ExtraMdcAspect.class));
    }
}
