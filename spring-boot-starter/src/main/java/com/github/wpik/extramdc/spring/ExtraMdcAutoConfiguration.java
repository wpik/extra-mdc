package com.github.wpik.extramdc.spring;

import com.github.wpik.extramdc.aspect.ExtraMdcAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ExtraMdcAspect.class, AbstractAutoProxyCreator.class})
@Slf4j
public class ExtraMdcAutoConfiguration {
    @Bean
    ExtraMdcAspect extraMdcAspect() {
        log.info("Auto configuring {}", ExtraMdcAspect.class.getName());
        return new ExtraMdcAspect();
    }
}
