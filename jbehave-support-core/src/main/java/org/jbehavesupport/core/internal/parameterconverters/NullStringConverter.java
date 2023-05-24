package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;

import org.jbehave.core.steps.ParameterConverters;
import org.jbehavesupport.core.internal.expression.NullCommand;
import org.springframework.stereotype.Component;

@Component
public class NullStringConverter extends ParameterConverters.FromStringParameterConverter<String> {

    @Override
    public boolean canConvertTo(Type type) {
        if (type instanceof Class<?>) {
            return String.class.isAssignableFrom((Class<?>) type);
        }
        return false;
    }

    @Override
    public String convertValue(String value, Type type) {
        if (NullCommand.NULL_VALUE.equals(value)) {
            return null;
        }
        return value;
    }
}
