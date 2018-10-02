package org.jbehavesupport.core.internal.converters;


import org.springframework.core.convert.converter.Converter;

public class StringToByteArrayConverter implements Converter<String, byte[]> {

    @Override
    public byte[] convert(String source) {
        return source.getBytes();
    }
}
