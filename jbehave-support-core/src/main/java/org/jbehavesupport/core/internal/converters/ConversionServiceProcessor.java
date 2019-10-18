package org.jbehavesupport.core.internal.converters;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversionServiceProcessor {

    private final ConfigurableConversionService conversionService;

    @PostConstruct
    public void init() {
        conversionService.addConverter(new ResourceToByteArrayConverter());
        conversionService.addConverter(new StringToByteArrayConverter());
        conversionService.addConverter(new LocalDateConverter());
        conversionService.addConverter(new LocalDateTimeConverter());
        conversionService.addConverter(new ZonedDateTimeConverter());
        conversionService.addConverter(new XmlGregorianCalendarConverter());
        conversionService.addConverterFactory(new StringToXmlEnumConverterFactory());
    }

}
