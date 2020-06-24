package io.github.wpik.extramdc.spring;

import io.github.wpik.extramdc.aspect.ExtraMdcAspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "extra-mdc.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass({ExtraMdcAspect.class, ProceedingJoinPoint.class})
@Slf4j
public class ExtraMdcAutoConfiguration {
    @Bean
    ExtraMdcAspect extraMdcAspect() {
        log.info("Auto configuring {}", ExtraMdcAspect.class.getName());
        return new ExtraMdcAspect();
    }
}
