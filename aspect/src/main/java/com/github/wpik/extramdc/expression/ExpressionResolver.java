package com.github.wpik.extramdc.expression;

public interface ExpressionResolver {
    String resolve(String expression, Object object);
}
