package org.jbehavesupport.core.internal.converters;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;

public class ResourceToByteArrayConverter implements Converter<Resource, byte[]> {

    @Override
    public byte[] convert(Resource source) {
        try {
            return IOUtils.toByteArray(source.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot get data from resource", e);
        }
    }
}
