package org.jbehavesupport.core.internal;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;

public class OnPropertyCondition implements ConfigurationCondition {

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(ConditionalOnProperty.class.getName());
        Environment environment = context.getEnvironment();
        String prefix = getPrefix(annotationAttributes);
        String name = getName(annotationAttributes);

        if (StringUtils.hasText(name)) {
            String propertyName = prefix + name;
            return environment.getProperty(propertyName) != null;
        } else {
            return ((ConfigurableEnvironment) environment).getPropertySources().stream()
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .map(EnumerablePropertySource::getPropertyNames)
                .flatMap(Arrays::stream)
                .anyMatch(propertyName -> propertyName.startsWith(prefix));
        }
    }

    private String getName(Map<String, Object> annotationAttributes) {
        String value = (String) annotationAttributes.get("value");
        String name = (String) annotationAttributes.get("name");
        return (StringUtils.hasText(value)) ? value : name;
    }

    private String getPrefix(Map<String, Object> annotationAttributes) {
        String prefix = (String) annotationAttributes.get("prefix");
        if (StringUtils.hasText(prefix) && !prefix.endsWith(".")) {
            prefix = prefix + ".";
        }

        return prefix;
    }

}
