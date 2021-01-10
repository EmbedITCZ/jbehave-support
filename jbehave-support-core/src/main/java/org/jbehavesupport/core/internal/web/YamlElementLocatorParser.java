package org.jbehavesupport.core.internal.web;

import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.internal.web.by.CssByFactory;
import org.jbehavesupport.core.web.ByFactory;
import org.jbehavesupport.core.web.ByFactoryResolver;
import org.jbehavesupport.core.web.WebElementRegistry;

import org.openqa.selenium.By;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class YamlElementLocatorParser {

    private static final String SEPARATOR = ".";
    private static final Pattern PATTERN = Pattern.compile("([^\\.]+)\\.?(.*)\\.([^\\.]+)");

    private final WebElementRegistry elementRegistry;
    private final ByFactoryResolver byFactoryResolver;
    private final CssByFactory defaultByCreator;

    private List<String> locatorTypes;

    @PostConstruct
    public void init() {
        locatorTypes = byFactoryResolver.getRegisteredTypes();
    }

    public void process(Resource elementLocatorsResource) {
        Properties properties = loadYamlProperties(elementLocatorsResource);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            try {
                processEntry(key, value);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(
                    "Unable to parse given entry [" + key + ": " + value + "] "
                        + "in file [" + elementLocatorsResource.getFilename() + "], "
                        + "the expected format is: pageName.elementName[.locatorType], "
                        + "where element name must be unique for page and "
                        + "locator type is optional, supported values are "
                        + "css (default) and xpath", e);
            }
        }
    }

    private void processEntry(String key, String value) {
        Matcher matcher = PATTERN.matcher(key);
        if (matcher.matches()) {
            String pageName = matcher.group(1);
            String middlePart = matcher.group(2).isEmpty() ? matcher.group(3) : matcher.group(2);
            String lastPart = matcher.group(2).isEmpty() ? null : matcher.group(3);

            String type = defaultByCreator.getType();
            if (locatorTypes.contains(lastPart)) {
                type = lastPart;
            } else {
                middlePart = Stream.of(middlePart, lastPart)
                    .filter(StringUtils::hasText)
                    .collect(joining(SEPARATOR));
            }

            elementRegistry.registerLocator(pageName, middlePart, by(type, value));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private By by(String type, String value) {
        ByFactory byFactory = byFactoryResolver.resolveByFactory(type);
        return byFactory.getBy(value);
    }

    private Properties loadYamlProperties(Resource elementLocatorsResource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(elementLocatorsResource);
        return yamlPropertiesFactoryBean.getObject();
    }

}
