package com.github.wpik.extramdc.expression;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

@Slf4j
public class SpelExpressionResolver implements ExpressionResolver {
    private ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public String resolve(String expression, Object object) {
        try {
            Expression expressionToEvaluate = expressionParser.parseExpression(expression);
            SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
            return expressionToEvaluate.getValue(context, object, String.class);
        } catch (Exception ex) {
            log.error("Exception occurred while tying to evaluate the SpEL expression [{}]", expression, ex);
            return String.valueOf(object);
        }
    }
}
