package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateTimeConverter extends ParameterConverters.FromStringParameterConverter<LocalDateTime> {
    @Override
    public boolean canConvertTo(Type type) {
        return type == LocalDateTime.class;
    }

    @Override
    public LocalDateTime convertValue(String s, Type type) {
        return LocalDateTime.parse(s);
    }
}
