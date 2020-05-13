package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalConverter implements ParameterConverters.ParameterConverter<BigDecimal> {
    @Override
    public boolean accept(Type type) {
        return type == BigDecimal.class;
    }

    @Override
    public BigDecimal convertValue(String s, Type type) {
        return new BigDecimal(s);
    }
}
