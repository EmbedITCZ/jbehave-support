package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class JBehaveBigDecimalConverter implements ParameterConverters.ParameterConverter {
    @Override
    public boolean accept(Type type) {
        return type == BigDecimal.class;
    }

    @Override
    public Object convertValue(String s, Type type) {
        return new BigDecimal(s);
    }
}
