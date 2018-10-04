package org.jbehavesupport.core.filtering;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import groovy.lang.GroovyClassLoader;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.ConfigurableEmbedder;
import org.jbehave.core.model.Meta;

/**
 * Builder helping to create scenario groovy filter based on clauses from annotations and external parameters.
 * Special behaviour for @feature metadata key is supported
 */
@Setter
@Getter
@Accessors(fluent = true)
@Slf4j
public class GroovyMetaFilterBuilder {
    // Static filters for ignoring stories and scenarios
    private static final String IGNORE_FILTER = "!ignore && !skip";

    private final Class clazz;

    // Custom filter written in groovy language passed either via property files or via system property. Optional.
    // Can be used to narrow test suite or execute particular tests
    // If specified, filter added as is via AND to composed filter
    private String metaFilter;

    // Delimited list of features enabled in the environment. Optional.
    // If specified, it enables test scenarios marked with @features metadata and containing all of required features
    // If not specified, scenarios marked with @features are not taken for execution. Scenarios not having @features are not affected
    private String features;

    // If we need to include standard filter for ignored items
    private boolean ignore = true;

    // Delimiter to splitting list of features and meta attributes
    private String delimiter = StringUtils.SPACE;


    /**
     * Creates builder
     *
     * @param clazz - test class to look for metafilter via @Metafilter annotation
     */
    public GroovyMetaFilterBuilder(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Builds MetaFilter with given parameters for given test class and parameters
     */
    public org.jbehave.core.embedder.MetaFilter build() {
        List<String> clauses = new ArrayList<>();

        // Adding filters passed bay calling code
        if (StringUtils.isNotBlank(metaFilter)) {
            clauses.add(metaFilter.trim());
        }

        // Adding features filter
        boolean excludeFeatures = true;
        if (StringUtils.isNotBlank(features)) {
            String[] split = features.split(delimiter);
            if (split.length > 0) {
                // Including features
                clauses.add("only('features', '" + StringUtils.join(split, "', '") + "')");
                excludeFeatures = false;
            }
        }
        if (excludeFeatures) {
            // Excluding scenarios marked with @features in meta
            clauses.add("!features");
        }

        // Adding predefined ignore/skip filters
        if (ignore) {
            clauses.add(IGNORE_FILTER);
        }

        // Adding filters from annotations from test class
        clauses.addAll(getAnnotatedMetafilters(clazz));

        // Building resulting expression
        String expression = GroovyMetaMatcher.GROOVY + StringUtils.join(clauses, ") && (");
        log.info("Filter expression {}", expression);

        return new org.jbehave.core.embedder.MetaFilter(expression,
            Collections.singletonMap(GroovyMetaMatcher.GROOVY, new GroovyMetaMatcher(delimiter)));
    }

    private List<String> getAnnotatedMetafilters(Class<? extends ConfigurableEmbedder> testClass) {
        List<String> result = new ArrayList<>();
        if (testClass != null && testClass.getAnnotation(MetaFilter.class) != null) {
            Collections.addAll(result, testClass.getAnnotation(MetaFilter.class).expressions());
        }
        return result;
    }

    /*
     *  Customized groovy meta matcher, with exists(), rall(), lall(), any() and matches()
     *  methods for checking presence of particular values in the meta attributes
     */
    private static class GroovyMetaMatcher implements org.jbehave.core.embedder.MetaFilter.MetaMatcher {
        public static final String GROOVY = "groovy:";

        private static final String MATCH_METHOD = "match";
        private static final String META_FIELD = "meta";

        private final String delimiter;

        private Class<?> groovyClass;
        private Field metaField;
        private Method match;

        public GroovyMetaMatcher() {
            this(StringUtils.SPACE);
        }

        public GroovyMetaMatcher(String delimiter) {
            this.delimiter = delimiter;
        }

        public void parse(String filterAsString) {
            try (GroovyClassLoader loader = new GroovyClassLoader(this.getClass().getClassLoader())) {
                this.groovyClass = loader.parseClass(
                    groovyClass(filterAsString.substring(GROOVY.length()))
                );


                this.match = this.groovyClass.getDeclaredMethod(MATCH_METHOD);
                this.metaField = this.groovyClass.getField(META_FIELD);
            } catch (NoSuchFieldException | NoSuchMethodException | IOException e) {
                throw new IllegalArgumentException("Can't parse filter expression", e);
            }
        }

        /**
         * Executes matching logic
         */
        public boolean match(Meta meta) {
            try {
                Object matcher = this.groovyClass.newInstance();
                this.metaField.set(matcher, meta);
                return (Boolean) this.match.invoke(matcher);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new IllegalArgumentException("Can't instantiate dynamic groovy matcher", e);
            }
        }

        protected String groovyClass(String filter) {
            return "public class GroovyMatcher {"
                + "public org.jbehave.core.model.Meta meta \n"

                + "public boolean match() {\n "
                + "     return ( " + filter + " )\n  "
                + "}\n  "
                // a.intersect(b) is not empty
                + "public boolean any(String key, String ... values) {\n"
                + "     return !exists(key) || !meta.getProperty(key).split('" + delimiter + "').toList().intersect(values.toList()).isEmpty() \n"
                + "}\n"

                // a.intersect(b) is empty
                + "public boolean none(String key, String ... values) {\n"
                + "     return !exists(key) || meta.getProperty(key).split('" + delimiter + "').toList().intersect(values.toList()).isEmpty() \n"
                + "}\n"

                // metadata[key].containsAll(values)
                + "public boolean all(String key, String ... values) {\n"
                + "     return !exists(key) || meta.getProperty(key).split('" + delimiter + "').toList().containsAll(values.toList()) \n"
                + "}\n"

                // values.containsAll(metadata[key])
                + "public boolean only(String key, String ... values) {\n"
                + "     return !exists(key) || values.toList().containsAll(meta.getProperty(key).split('" + delimiter + "').toList()) \n"
                + "}\n"

                // Equals
                + "public boolean eq(String key, String value) {\n"
                + "     return !exists(key) || meta.getProperty(key) == value;\n"
                + "}\n"

                // Reg exp match
                + "public boolean matches(String key, String regExp) {\n"
                + "     return exists(key) && meta.getProperty(key) ==~ regExp;\n"
                + "}\n"

                // Presence of property
                + "public boolean exists(String key) {\n"
                + "     return meta.hasProperty(key) \n"
                + "}\n"

                + "def propertyMissing(String name) {\n"
                + "     if (!exists(name)) {\n"
                + "           return false\n    "
                + "     }\n    "
                + "     def v = meta.getProperty(name)\n    "
                + "     if (v.equals('')) {\n"
                + "           return true\n    "
                + "     }\n    "
                + "     return v\n  "
                + "}\n"
                + "}";
        }
    }
}
