package org.jbehavesupport.core.internal;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.isTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.jbehavesupport.core.TestContext;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jbehavesupport.core.AbstractSpringStories;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestContextImpl implements TestContext {
    private static final String REFERENCE_PREFIX = "*_reference_";
    private static final Pattern REFERENCE_PREFIX_PATTERN = Pattern.compile("\\*_reference_\\d+");
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private Map<String, ValueHolder> context = new HashMap<>();

    private final ConversionService conversionService;

    @Override
    public <T> T get(String key) {
        return (T) getValueHolder(key).getValue();
    }

    private ValueHolder getValueHolder(String key) {
        isTrue(context.containsKey(key), "Test context doesn't contain key: " + key);
        Object value = context.get(key).getValue();
        if (nonNull(value) && value instanceof String && isReferenceKey((String) value)) {
            return context.get(value);
        } else {
            return context.get(key);
        }
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return conversionService.convert(get(key), type);
    }

    @Override
    public Entry getEntry(String key) {
        return new EntryImpl(key, getValueHolder(key));
    }

    @Override
    public void put(String key, Object value) {
        context.put(key, new ValueHolder(value, null));
    }

    @Override
    public void put(String key, Object value, Metadata... metadata) {
        context.put(key, new ValueHolder(value, Arrays.asList(metadata)));
    }

    @Override
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    @Override
    public <T> T remove(String key) {
        return (T) context.remove(key).getValue();
    }

    @Override
    public void clear() {
        context.entrySet().removeIf(entry -> !AbstractSpringStories.JBEHAVE_SCENARIO.equals(entry.getKey()));
        log.info("Test context cleared.");
    }

    @Override
    public void clear(Predicate<String> p) {
        keySet().stream()
            .filter(p)
            .forEach(this::remove);
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(new HashSet<>(context.keySet()));
    }

    @Override
    public String createReferenceKey() {
        return REFERENCE_PREFIX + COUNTER.getAndIncrement();
    }

    @Override
    public boolean isReferenceKey(String referenceKey) {
        if (nonNull(referenceKey)) {
            return REFERENCE_PREFIX_PATTERN.matcher(referenceKey).matches();
        } else {
            return false;
        }
    }

    @Value
    private class ValueHolder {
        Object value;
        List<Metadata> metadata;

        List<Metadata> getMetadata() {
            return metadata != null ? Collections.unmodifiableList(metadata) : Collections.emptyList();
        }
    }

    @Value
    private class EntryImpl implements Entry {
        String key;
        ValueHolder valueHolder;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public <T> T getValue() {
            return (T) valueHolder.getValue();
        }

        @Override
        public List<Metadata> getMetadata() {
            return valueHolder.getMetadata();
        }
    }
}
