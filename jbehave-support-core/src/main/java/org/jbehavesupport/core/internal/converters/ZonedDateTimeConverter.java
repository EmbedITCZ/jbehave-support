package org.jbehavesupport.core.internal.converters;

import java.time.ZonedDateTime;

import org.springframework.core.convert.converter.Converter;

public class ZonedDateTimeConverter extends AbstractChronoConverter implements Converter<String, ZonedDateTime> {

    @Override
    public ZonedDateTime convert(final String source) {
        return ZonedDateTime.from(parseBest(source));
    }

}
