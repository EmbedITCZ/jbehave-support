package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeConverter implements ParameterConverters.ParameterConverter<LocalDateTime> {
    @Override
    public boolean accept(Type type) {
        return type == LocalDateTime.class;
    }

    @Override
    public LocalDateTime convertValue(String s, Type type) {
        return LocalDateTime.parse(s);
    }
}
