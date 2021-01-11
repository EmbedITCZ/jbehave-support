package org.jbehavesupport.core.support;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.ClassUtils.isAssignable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StringUtils.hasText;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.namespace.QName;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.jbehavesupport.core.internal.expression.NilCommand;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

/**
 * The {@code RequestFactory} is responsible for creation of request objects and setting their values specified in {@code TestContext}.
 * It uses test context key as bean path, so the names used in keys should correspond with names in java classes.
 * It also performs necessary conversions to correct types, it is possible to provide custom conversion, via {@link ConversionService}
 * <p>
 * Example:
 * <p>
 * Suppose test story contains following step, which put data into text context:
 * <pre>
 * Given CreateClientRequest data:
 * | name                      | data                    |
 * | client.firstName          | {RANDOM_STRING:10}      |
 * | client.lastName           | {RANDOM_STRING:10}      |
 * | client.birthDate          | {RANDOM_DATE}           |
 * | client.addresses.0.city   | Praha                   |
 * | client.addresses.0.type   | HOME                    |
 * | client.addresses.1.city   | Brno                    |
 * | client.addresses.1.type   | BILLING                 |
 * </pre>
 * <p>
 * Then the RequestFactory is able to create and initialize instance of request class with following structure:
 *
 * <pre>
 * &#064;Data
 * class CreateClientRequest {
 *     Client client;
 * }
 *
 * &#064;Data
 * class Client {
 *     String firstName;
 *     String lastName;
 *     LocalDate birthDate;
 *     List&lt;Address&gt; addresses;
 * }
 *
 * &#064;Data
 * class Address {
 *     String city;
 *     String type;
 * }
 * </pre>
 */
@Getter
@SuppressWarnings("squid:S00119")
public class RequestFactory<REQUEST> {

    private static final String MISSING_FIELD_MESSAGE = "There is no %s in class %s, please check keys in test story";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm:ss[xxx]]");

    private final Class<REQUEST> requestClazz;
    private final TestContext testContext;
    private final ConversionService conversionService;
    private final Map<String, Object> factoryContext = new LinkedHashMap<>();
    private final Map<String, Class> parameterizedTypes = new LinkedHashMap<>();
    private final Map<String, Consumer<REQUEST>> customHandlers = new LinkedHashMap<>();
    private final Map<String, String> overrides = new LinkedHashMap<>();

    private String prefix;
    private String pathStep = null;
    private String pathCurrent = null;
    private String pathPrevious = null;
    private AccessStrategy accessStrategy;

    public RequestFactory(Class<REQUEST> requestClazz, TestContext testContext, ConversionService conversionService) {
        requireNonNull(testContext);
        requireNonNull(requestClazz);
        requireNonNull(conversionService);
        this.requestClazz = requestClazz;
        this.testContext = testContext;
        this.conversionService = conversionService;
        this.accessStrategy = new BeanAccessStrategy();
    }

    public RequestFactory withFieldAccessStrategy() {
        accessStrategy = new FieldAccessStrategy();
        return this;
    }

    public RequestFactory<REQUEST> prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public RequestFactory<REQUEST> override(String key, String path) {
        overrides.put(key, path);
        return this;
    }

    public RequestFactory<REQUEST> handler(String key, Consumer<REQUEST> handler) {
        customHandlers.put(key, handler);
        return this;
    }

    public REQUEST createRequest() {
        if (prefix == null) {
            prefix = requestClazz.getSimpleName();
        }
        try {
            REQUEST request = requestClazz.newInstance();
            factoryContext.put(prefix, request);
            handleKeys();
            return request;
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new IllegalStateException("Unable to create request " + requestClazz.getSimpleName(), e);
        }
    }

    private void handleKeys() throws ReflectiveOperationException {
        List<String> keys = testContext.keySet()
            .stream()
            .filter(key -> key.startsWith(prefix))
            .filter(key -> !key.startsWith(prefix + ".@header"))
            .sorted()
            .collect(toList());

        for (String key : keys) {
            if (customHandlers.containsKey(key)) {
                customHandler(key);
            } else {
                handleKey(key);
            }
        }
    }

    private void customHandler(String key) {
        Consumer<REQUEST> customHandler = customHandlers.get(key);
        REQUEST request = (REQUEST) factoryContext.get(prefix);
        customHandler.accept(request);
    }

    private void handleKey(String key) throws ReflectiveOperationException {
        String path = resolveOverrides(key);
        pathStep = null;
        pathCurrent = null;
        pathPrevious = null;

        Iterator<String> pathSteps = Stream.of(StringUtils.split(path, ".")).iterator();
        while (pathSteps.hasNext()) {
            pathStep = pathSteps.next();
            pathPrevious = pathCurrent;
            pathCurrent = Stream.of(pathCurrent, pathStep)
                .filter(s -> hasText(s))
                .collect(joining("."));

            if (!factoryContext.containsKey(pathCurrent)) {
                if (isLastStep(pathSteps)) {
                    handleLastStep(key);
                } else if (isCollectionIndexStep()) {
                    handleCollectionIndexStep();
                } else {
                    handleNestedStep();
                }
            }
        }
    }

    private void handleLastStep(String key) {
        Object target = factoryContext.get(pathPrevious);
        Class type = parameterizedTypes.containsKey(pathPrevious) ? parameterizedTypes.get(pathPrevious) : accessStrategy.getType(target, pathStep);
        Object value = resolveValue(testContext, key, handleJaxbType(type));
        if (isCollectionIndexStep()) {
            Object collection = factoryContext.get(pathPrevious);
            ((Collection) collection).add(value);
        } else {
            accessStrategy.setValue(unwrapJaxbValue(target), value, pathStep);
        }
        factoryContext.put(pathCurrent, value);
    }

    private void handleCollectionIndexStep() throws ReflectiveOperationException {
        Class type = parameterizedTypes.get(pathPrevious);
        if (type == null) {
            throw new IllegalStateException("path " + pathPrevious + " has unresolved type");
        }
        if (isAbstract(type)) {
            throw new UnsupportedOperationException("Abstract classes are not supported as generic");
        }
        Object value = type.newInstance();
        Object collection = factoryContext.get(pathPrevious);
        ((Collection) collection).add(value);
        factoryContext.put(pathCurrent, value);
    }

    private void handleNestedStep() throws ReflectiveOperationException {
        Object target = factoryContext.get(pathPrevious);
        Object value = accessStrategy.getValue(target, pathStep);
        Class type = accessStrategy.getType(target, pathStep);
        if (value == null) {
            if (type.isInterface()) {
                value = resolveInterfaceInstance(pathStep, type);
            } else if (isAssignable(type, JAXBElement.class)) {
                value = resolveJaxbElement(testContext, pathCurrent, null);
            } else if (!isAbstract(type)) {
                value = type.newInstance();
            } else {
                throw new IllegalStateException(
                    "Field " + pathCurrent + " is abstract. Please provide fully qualified class name in example table or register custom handler");
            }
            accessStrategy.setValue(target, value, pathStep);
        }
        if (isGeneric(type)) {
            resolveParametrizedType(target, pathStep);
        }
        factoryContext.put(pathCurrent, value);
    }

    private Object resolveInterfaceInstance(String pathStep, Class type) {
        if (isAssignable(type, Set.class)) {
            return new LinkedHashSet<>();
        } else if (isAssignable(type, List.class)) {
            return new ArrayList<>();
        } else {
            throw new UnsupportedOperationException("Unsupported type of property" + pathStep);
        }
    }

    private void resolveParametrizedType(Object target, String pathStep) {
        if (!parameterizedTypes.containsKey(pathCurrent)) {
            Class<?> type = accessStrategy.getType(target, pathStep);
            if (isAnyAssignable(type, Set.class, List.class, JAXBElement.class)) {
                try {
                    Class typeParameter = accessStrategy.getGenericType(target, pathStep);
                    parameterizedTypes.put(pathCurrent, typeParameter);
                } catch (ClassCastException e) {
                    throw new UnsupportedOperationException("Nested generic types are not supported", e);
                }
            }
        }
    }

    private boolean isAnyAssignable(Class<?> sourceType, Class<?>... targetTypes) {
        Assert.notNull(sourceType, "source type must be provided");
        Assert.notEmpty(targetTypes, "target types must be provided");
        for (Class<?> targetType : targetTypes) {
            if (isAssignable(sourceType, targetType)) {
                return true;
            }
        }
        return false;
    }

    private Object resolveValue(TestContext testContext, String key, Class type) {
        Object value = testContext.get(key);
        Class<?> metaType = getMetaType(testContext, key);
        if (metaType != null) {
            return instantiateClass(metaType);
        }
        if (isAssignable(type, JAXBElement.class)) { //Very tricky to extract to converter
            value = resolveJaxbElement(testContext, key, value);
        } else if (value == null) {
            value = instantiateClass(type);
        } else if (conversionService.canConvert(value.getClass(), type)) {
            value = conversionService.convert(value, type);
        }
        assertThat(value == null && isAbstract(type)).withFailMessage("Please specify type for " + key).isFalse();
        return value;
    }

    /**
     * This handles jaxb element on type that is wrapped inside another jaxb element.
     * Unwraps parent Jaxb Element attribute
     * @return type of generic Jaxb Element
     */
    private Class handleJaxbType(Class type) {
        if (isAssignable(factoryContext.get(pathPrevious).getClass(), JAXBElement.class)) {
            return accessStrategy.getType(((JAXBElement) factoryContext.get(pathPrevious)).getValue(), pathStep);
        }
        return type;
    }

    /**
     * Unwraps Jaxb Element
     * @param value to be unwrapped
     * @return native object from Jaxb Element
     */
    private Object unwrapJaxbValue(Object value) {
        if (isAssignable(value.getClass(), JAXBElement.class)) {
            return ((JAXBElement) value).getValue();
        }
        return value;
    }

    private Class<?> getMetaType(TestContext testContext, String key) {
        return (Class<?>) testContext.getEntry(key).getMetadata().stream()
            .filter(MetadataUtil::isType)
            .findFirst()
            .orElse(TestContext.Metadata.of("TYPE", null))
            .getValue();
    }

    private Object resolveJaxbElement(final TestContext testContext, final String key, Object value) {
        Class genericType = accessStrategy.getGenericType(factoryContext.get(pathPrevious), pathStep);
        XmlElementRef xmlElemRefAnn = getXmlElementRef();

        if (value == null) {
            return new JAXBElement(new QName(xmlElemRefAnn.namespace(), pathStep), genericType, instantiateClass(genericType));
        } else if (NilCommand.NIL.equals(value)) {
            JAXBElement ret = new JAXBElement(new QName(xmlElemRefAnn.namespace(), pathStep), genericType, null);
            ret.setNil(true);
            return ret;
        } else {
            return new JAXBElement(new QName(xmlElemRefAnn.namespace(), pathStep), genericType, resolveValue(testContext, key, genericType));
        }
    }

    private XmlElementRef getXmlElementRef() {
        Object parentInstance = unwrapJaxbValue(factoryContext.get(pathPrevious));
        Field annotatedField = ReflectionUtils.getFieldsIncludingSuperclasses(parentInstance.getClass()).stream()
            .filter(f -> f.getName().equalsIgnoreCase(pathStep))
            .reduce((a, b) -> {
                if (b != null) {
                    throw new UnsupportedOperationException("multiple fields match name condition: " + pathStep);
                }
                return a;
            })
            .orElseThrow(() -> new IllegalStateException("no fields with name " + pathStep + " found in " + parentInstance.getClass().getSimpleName()));

        return annotatedField.getAnnotation(XmlElementRef.class);
    }

    private Object instantiateClass(Class<?> toInstantiate) {
        try {
            return toInstantiate.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Provided implementation is not accessible: " + toInstantiate.getSimpleName(), e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Provided implementation could not be instantiated: " + toInstantiate.getSimpleName(), e);
        }
    }

    private boolean isAbstract(Class clazz) {
        return clazz != null
            && !clazz.isPrimitive() && Modifier.isAbstract(clazz.getModifiers());
    }

    private String resolveOverrides(String key) {
        String path = key;
        for (Map.Entry<String, String> entry : overrides.entrySet()) {
            path = path.replace(entry.getKey(), entry.getValue());
        }
        return path;
    }

    private boolean isLastStep(Iterator<String> pathSteps) {
        return !pathSteps.hasNext();
    }

    private boolean isCollectionIndexStep() {
        return StringUtils.isNumeric(pathStep);
    }

    private boolean isGeneric(final Class type) {
        return isNotEmpty(type.getTypeParameters());
    }

    interface AccessStrategy {

        Class getType(Object target, String pathStep);

        void setValue(Object target, Object value, final String pathStep);

        Object getValue(Object target, String pathStep);

        Class getGenericType(Object target, String pathStep);
    }

    class BeanAccessStrategy implements AccessStrategy {

        @Override
        public Class getType(final Object target, final String pathStep) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(target);
            PropertyDescriptor property = resolveProperty(beanWrapper, pathStep);
            return property.getPropertyType();
        }

        @Override
        public void setValue(final Object target, final Object value, final String pathStep) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(target);
            beanWrapper.setPropertyValue(pathStep, value);
        }

        @Override
        public Object getValue(final Object target, final String pathStep) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(target);
            return beanWrapper.getPropertyValue(pathStep);
        }

        @Override
        @SuppressWarnings("squid:S1166")
        public Class getGenericType(final Object target, final String pathStep) {
            BeanWrapper beanWrapper = new BeanWrapperImpl(unwrapJaxbValue(target));
            PropertyDescriptor property = resolveProperty(beanWrapper, pathStep);
            Method getter = property.getReadMethod();
            Type genericType = getter.getGenericReturnType();
            return (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }

        @SuppressWarnings("squid:S1166")
        private PropertyDescriptor resolveProperty(BeanWrapper beanWrapper, String propertyName) {
            try {
                return beanWrapper.getPropertyDescriptor(propertyName);
            } catch (InvalidPropertyException e) {
                throw new NoSuchElementException(
                    String.format(MISSING_FIELD_MESSAGE, "property " + propertyName, beanWrapper.getWrappedClass().getSimpleName()));
            }
        }
    }

    class FieldAccessStrategy implements AccessStrategy {

        @Override
        public Class getType(final Object target, final String pathStep) {
            return getField(target, pathStep).getType();
        }

        @Override
        public void setValue(final Object target, final Object value, final String pathStep) {
            try {
                FieldUtils.writeField(getField(target, pathStep), target, value, true);
            } catch (IllegalAccessException e) {
                throw new RejectedExecutionException("Access should have been forced", e);
            }
        }

        @Override
        public Object getValue(final Object target, final String pathStep) {
            try {
                return FieldUtils.readField(getField(target, pathStep), target, true);
            } catch (IllegalAccessException e) {
                throw new RejectedExecutionException("Access should have been forced", e);
            }
        }

        @Override
        public Class getGenericType(final Object target, final String pathStep) {
            Field field = getField(unwrapJaxbValue(target), pathStep);
            return org.jbehavesupport.core.internal.ReflectionUtils.getGenericClass(field);
        }

        private Field getField(final Object target, String pathStep) {
            Field field = FieldUtils.getDeclaredField(target.getClass(), pathStep, true);
            if (field == null) {
                throw new NoSuchElementException(String.format(MISSING_FIELD_MESSAGE, "field " + pathStep, target.getClass().getSimpleName()));
            }
            return field;
        }
    }

}
