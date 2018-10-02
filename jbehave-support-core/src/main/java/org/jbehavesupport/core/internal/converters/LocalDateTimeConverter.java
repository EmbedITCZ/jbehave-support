package org.jbehavesupport.core.internal.converters;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;

public class LocalDateTimeConverter extends AbstractChronoConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(final String source) {
        return LocalDateTime.from(parseBest(source));
    }

}
