package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeConverter implements ParameterConverters.ParameterConverter {
    @Override
    public boolean accept(final Type type) {
        return type == LocalDateTime.class;
    }

    @Override
    public Object convertValue(final String s, final Type type) {
        return LocalDateTime.parse(s);
    }
}
