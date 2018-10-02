package org.jbehavesupport.core.internal.web.property;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.jbehavesupport.core.web.WebProperty;
import org.jbehavesupport.core.web.WebPropertyResolver;

import org.springframework.stereotype.Component;

@Component
public class WebPropertyResolverImpl implements WebPropertyResolver {

    private final Map<String, WebProperty> properties;

    public WebPropertyResolverImpl(List<WebProperty> properties) {
        this.properties = properties.stream()
            .collect(toMap(e -> e.name(), e -> e));
    }

    @Override
    public <T> WebProperty<T> resolveProperty(String propertyName) {
        WebProperty property = properties.get(propertyName);
        if (property == null) {
            throw new IllegalArgumentException("Unable to resolve web property with name " + propertyName);
        }
        return property;
    }

}
