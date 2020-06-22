package io.github.wpik.extramdc.aspect;

import io.github.wpik.extramdc.annotation.MdcField;
import io.github.wpik.extramdc.expression.ExpressionResolver;
import io.github.wpik.extramdc.expression.SpelExpressionResolver;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;

@Aspect
@Slf4j
public class ExtraMdcAspect {
    private ExpressionResolver expressionResolver = new SpelExpressionResolver();

    @Around("@annotation(io.github.wpik.extramdc.annotation.ExtraMdc)")
    public Object extendMDC(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();

        Collection<String> keys = null;
        try {
            keys = addParameterDefinedValues(method, pjp.getArgs());
            return pjp.proceed();
        } finally {
            removeParameterDefinedValues(keys);
        }
    }

    private Collection<String> addParameterDefinedValues(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Collection<String> keys = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Object arg = args[i];
            Parameter parameter = parameters[i];
            MergedAnnotations mergedAnnotations = MergedAnnotations.from(parameter);
            mergedAnnotations.stream(MdcField.class)
                    .forEach(mdcField -> fillMdc(keys, arg, mdcField, method, parameter));
        }
        return keys;
    }

    private void fillMdc(
            Collection<String> keys, Object arg, MergedAnnotation<MdcField> mdcField,
            Method method, Parameter parameter
    ) {
        String name = mdcField.getString("value");
        if (StringUtils.hasLength(name)) {
            MDC.put(name, resolveValue(mdcField.getString("expression"), arg));
            keys.add(name);
        } else {
            log.warn("Invalid MdcField name defined for parameter {} of {}.{}. Ignoring this field.",
                    parameter.getName(), method.getDeclaringClass().getCanonicalName(), method.getName());
        }
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
