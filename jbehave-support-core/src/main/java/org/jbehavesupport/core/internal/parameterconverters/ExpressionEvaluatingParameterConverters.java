package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.expression.ExpressionEvaluator;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ExpressionEvaluatingParameterConverters extends ParameterConverters {

    @Autowired
    private ExpressionEvaluator expressionEvaluator;

    @Autowired(required = false)
    private List<ParameterConverter> converters;

    @PostConstruct
    public void init() {
        addConverters(converters);
    }

    @Override
    public Object convert(String value, Type type) {
        if (isExpressionEvaluatingParameter(type)) {
            return new ExpressionEvaluatingParameter<>(
                super.convert(
                    String.valueOf(expressionEvaluator.evaluate(value)),
                    ((ParameterizedType) type).getActualTypeArguments()[0]
                )
            );
        }
        return super.convert(value, type);
    }

    private boolean isExpressionEvaluatingParameter(Type type) {
        return type instanceof ParameterizedType
            && ((ParameterizedType) type).getRawType() == ExpressionEvaluatingParameter.class;
    }
}
