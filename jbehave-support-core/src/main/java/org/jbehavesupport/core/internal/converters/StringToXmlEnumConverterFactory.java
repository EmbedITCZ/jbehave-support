package org.jbehavesupport.core.internal.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.util.Assert;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import java.lang.reflect.Field;

import static java.util.Arrays.stream;

/**
 * Improved copy of Spring {@link org.springframework.core.convert.support.StringToEnumConverterFactory}.
 */
public class StringToXmlEnumConverterFactory implements ConverterFactory<String, Enum> {

	@Override
	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnum(getEnumType(targetType));
	}

    private Class<?> getEnumType(Class<?> targetType) {
        Class<?> enumType = targetType;
        while (enumType != null && !enumType.isEnum()) {
            enumType = enumType.getSuperclass();
        }
        Assert.notNull(enumType, () -> "The target type " + targetType.getName() + " does not refer to an enum");
        return enumType;
    }

	private static class StringToEnum<T extends Enum> implements Converter<String, T> {

		private final Class<T> enumType;

		public StringToEnum(Class<T> enumType) {
			this.enumType = enumType;
		}

		@Override
		public T convert(String source) {
			if (source.isEmpty()) {
				// It's an empty enum identifier: reset the enum value to null.
				return null;
			}

            return (T) Enum.valueOf(this.enumType,  getEnumFieldName(source.trim()));
		}

		private String getEnumFieldName(String source) {
		    if (enumType.getDeclaredAnnotation(XmlEnum.class) == null) {
		        return source;
            }

            return stream(enumType.getDeclaredFields())
                .filter(field -> stream(field.getAnnotationsByType(XmlEnumValue.class))
                    .anyMatch(xmlEnumValue -> source.equals(xmlEnumValue.value())))
                .map(Field::getName)
                .findFirst()
                .orElse(source);
        }

	}

}
