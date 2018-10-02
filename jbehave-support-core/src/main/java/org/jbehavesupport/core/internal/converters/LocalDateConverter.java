package org.jbehavesupport.core.internal.converters;

import java.time.LocalDate;

import org.springframework.core.convert.converter.Converter;

public class LocalDateConverter extends AbstractChronoConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(final String source) {
        return LocalDate.from(parseBest(source));
    }

}
