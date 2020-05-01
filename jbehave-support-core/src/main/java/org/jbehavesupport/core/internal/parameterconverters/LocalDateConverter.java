package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDate;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class LocalDateConverter implements ParameterConverters.ParameterConverter<LocalDate> {
    @Override
    public boolean accept(Type type) {
        return type == LocalDate.class;
    }

    @Override
    public LocalDate convertValue(String s, Type type) {
        return LocalDate.parse(s);
    }
}
