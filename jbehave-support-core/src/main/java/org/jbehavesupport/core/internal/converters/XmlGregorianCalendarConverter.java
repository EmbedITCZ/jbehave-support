package org.jbehavesupport.core.internal.converters;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.core.convert.converter.Converter;

public class XmlGregorianCalendarConverter extends AbstractChronoConverter implements Converter<String, XMLGregorianCalendar> {

    @Override
    public XMLGregorianCalendar convert(final String source) {
        return convertToXMLGregorianCalendar(source);
    }
}
