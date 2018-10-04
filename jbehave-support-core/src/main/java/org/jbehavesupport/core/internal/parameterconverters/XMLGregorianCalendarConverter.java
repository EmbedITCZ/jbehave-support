package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
public class XMLGregorianCalendarConverter implements ParameterConverters.ParameterConverter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("[dd/MM/yyyy][yyyy-MM-dd]");

    @Override
    public boolean accept(Type type) {
        return XMLGregorianCalendar.class == type;
    }

    @Override
    public Object convertValue(String s, Type type) {
        return convert(LocalDate.parse(s, DATE_TIME_FORMATTER));
    }

    private XMLGregorianCalendar convert(LocalDate localDate) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(
                localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
