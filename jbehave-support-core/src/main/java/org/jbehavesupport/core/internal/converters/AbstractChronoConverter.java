package org.jbehavesupport.core.internal.converters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

abstract class AbstractChronoConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss[xxx]]");
    private static DatatypeFactory datatypeFactory;

    AbstractChronoConverter() {
    }

    static XMLGregorianCalendar convertToXMLGregorianCalendar(String value) {
        return getDatatypeFactory().newXMLGregorianCalendar(DATE_TIME_FORMATTER.format(parseBest(value)));
    }

    static TemporalAccessor parseBest(final String value) {
        return DATE_TIME_FORMATTER.parseBest(value, ZonedDateTime::from, LocalDateTime::from, LocalDate::from);
    }

    private static DatatypeFactory getDatatypeFactory() {
        if (datatypeFactory == null) {
            try {
                datatypeFactory = DatatypeFactory.newInstance();
            } catch (DatatypeConfigurationException e) {
                throw new IllegalArgumentException("unable to instantiate datatype factory", e);
            }
        }
        return datatypeFactory;
    }
}
