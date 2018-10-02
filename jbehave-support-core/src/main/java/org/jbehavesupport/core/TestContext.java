package org.jbehavesupport.core;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import lombok.Value;

/**
 * The context for holding data through scenario run.
 */
public interface TestContext {

    /**
     * Entry in the test context.
     */
    interface Entry {
        String getKey();
        <T> T getValue();
        List<Metadata> getMetadata();
    }

    /**
     * Metadata holder.
     */
    @Value(staticConstructor="of")
    class Metadata {
        String name;
        Object value;
    }

    /**
     * Get object for specific key.
     * Key must exists in internal structure.
     * When the key is reference method returns referenced object.
     *
     * @param key
     * @param <T> returned type
     * @return value from context
     */
    <T> T get(String key);

    /**
     * Get object for specific key converted to specific type.
     * @param key
     * @param type target type
     * @param <T>
     * @return value from context in specific type
     */
    <T> T get(String key, Class<T> type);

    /**
     * Get entry from context.
     * @param key
     * @param <T>
     * @return entry
     */
    Entry getEntry(String key);

    /**
     * Put data under specific key.
     *
     * @param key the key
     * @param value the value
     */
    void put(String key, Object value);

    /**
     * Put data under specific key with metadata.
     *
     * @param key the key
     * @param value the value
     * @param metadata the metadata
     */
    void put(String key, Object value, Metadata... metadata);

    /**
     * Check if context contains specific key.
     *
     * @param key the key
     * @return the boolean
     */
    boolean contains(String key);

    /**
     * Remove specific entry from context
     * @param key the key
     * @param <T>
     * @return removed object
     */
    <T> T remove(String key);

    /**
     * Clear context data.
     */
    void clear();

    /**
     * Remove data from context based on predicate.
     * @param p
     */
    void clear(Predicate<String> p);

    /**
     * Get set of keys.
     * @return
     */
    Set<String> keySet();

    /**
     * Create unique key which will be used as reference.
     * @return
     */
    String createReferenceKey();

    /**
     * Check if provided key is the reference.
     * @param referenceKey
     * @return
     */
    boolean isReferenceKey(String referenceKey);
}
