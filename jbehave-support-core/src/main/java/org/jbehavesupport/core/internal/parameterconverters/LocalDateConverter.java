package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDate;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateConverter extends ParameterConverters.FromStringParameterConverter<LocalDate> {
    @Override
    public boolean canConvertTo(Type type) {
        return type == LocalDate.class;
    }

    @Override
    public LocalDate convertValue(String s, Type type) {
        return LocalDate.parse(s);
    }
}
