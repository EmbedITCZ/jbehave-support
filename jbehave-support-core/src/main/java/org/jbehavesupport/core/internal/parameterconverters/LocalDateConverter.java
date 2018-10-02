package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDate;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateConverter implements ParameterConverters.ParameterConverter{
    @Override
    public boolean accept(final Type type) {
        return type == LocalDate.class;
    }

    @Override
    public Object convertValue(final String s, final Type type) {
        return LocalDate.parse(s);
    }
}
