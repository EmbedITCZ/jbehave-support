package org.jbehavesupport.core.internal;

import static org.apache.http.util.Asserts.notEmpty;
import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ReflectionUtils.doWithFields;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class ReflectionUtils {

    /**
     * Returns all property names
     *
     * @param type
     * @return
     */
    public static List<String> getAllPropertyNames(Class<?> type) {
        notNull(type, "type cannot be null");
        List<String> fields = new ArrayList<>();
        doWithFields(type, f -> fields.add(f.getName()));
        return fields;
    }

    /**
     * Checks whether class contains fields as specified in the argument
     *
     * @param clazz
     * @param fieldsToVerify
     */
    public static void verifyHasPropertiesForFields(Class<?> clazz, List<String> fieldsToVerify) {
        List<String> fieldsToVerifyCopy = new ArrayList<>(fieldsToVerify);
        fieldsToVerifyCopy.removeAll(getAllPropertyNames(clazz));
        if (!fieldsToVerifyCopy.isEmpty()) {
            throw new IllegalStateException(
                String.format("Target parameter class - %s does not contain the following properties: [%s]",
                    clazz, fieldsToVerifyCopy.stream().collect(Collectors.joining(", "))
                )
            );
        }
    }

    /**
     * Travers object tree structure by property path
     *
     * @param bean         root object to start with
     * @param propertyPath dot separated path of attributes
     * @return value of located attribute
     */
    @SuppressWarnings("squid:S1166")
    public static Object getPropertyValue(Object bean, String propertyPath) {
        notNull(propertyPath, "property path must be specified");
        notEmpty(propertyPath, "property path");
        Object beanAttribute = bean;
        try {
            for (String currentPath : propertyPath.split("\\.")) {
                if (StringUtils.isNumeric(currentPath)) {
                    beanAttribute = resolveCollection(beanAttribute, currentPath);
                } else {
                    beanAttribute = getPropertyDescriptor(beanAttribute.getClass(), currentPath)
                        .getReadMethod()
                        .invoke(beanAttribute);
                }
            }
            return beanAttribute;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Requested field from path " + propertyPath + " probably does not exist, or isn't set", e);
        }
    }

    private static Object resolveCollection(Object beanAttribute, final String pathPart) throws ReflectiveOperationException {
        if (beanAttribute instanceof Set) {
            List list = new ArrayList<Object>((Set) beanAttribute);
            return list.get(Integer.valueOf(pathPart));
        } else {
            return beanAttribute.getClass()
                .getMethod("get", int.class)
                .invoke(beanAttribute, Integer.valueOf(pathPart));
        }
    }


    public static Class getGenericClass(Field field) {
        return (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

}
