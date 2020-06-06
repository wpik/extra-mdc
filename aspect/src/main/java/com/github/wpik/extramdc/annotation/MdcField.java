package com.github.wpik.extramdc.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MdcFields.class)
public @interface MdcField {
    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String expression() default "";
}
