package com.github.wpik.extramdc.aspect;

import com.github.wpik.extramdc.annotation.MdcField;
import com.github.wpik.extramdc.expression.ExpressionResolver;
import com.github.wpik.extramdc.expression.SpelExpressionResolver;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

@Aspect
public class ExtraMdcAspect {
    private ExpressionResolver expressionResolver = new SpelExpressionResolver();

    @Around("@annotation(com.github.wpik.extramdc.annotation.ExtraMdc)")
    public Object extendMDC(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        Collection<String> keys = null;
        try {
            keys = addParameterDefinedValues(method.getParameters(), pjp.getArgs());
            return pjp.proceed();
        } finally {
            removeParameterDefinedValues(keys);
        }
    }

    private Collection<String> addParameterDefinedValues(Parameter[] parameters, Object[] args) {
        Collection<String> keys = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            MdcField mdcField = parameter.getAnnotation(MdcField.class);
            if (mdcField != null) {
                MDC.put(mdcField.name(), resolveValue(mdcField.expression(), args[i]));
                keys.add(mdcField.name());
            }
        }
        return keys;
    }

    private String resolveValue(String expression, Object o) {
        if (StringUtils.hasLength(expression)) {
            return expressionResolver.resolve(expression, o);
        } else {
            return String.valueOf(o);
        }
    }

    private void removeParameterDefinedValues(Collection<String> keys) {
        if (keys != null) {
            keys.forEach(MDC::remove);
        }
    }
}
