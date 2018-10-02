package org.jbehavesupport.core.internal.web;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jbehavesupport.core.web.WebElementRegistry;

import org.openqa.selenium.By;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class YamlElementLocatorParser {

    private static final String SEPARATOR = ".";
    private static final Pattern PATTERN = Pattern.compile("([^\\.]+)\\.?(.*)\\.([^\\.]+)");
    private static final String CSS_LOCATOR_TYPE = "css";
    private static final String XPATH_LOCATOR_TYPE = "xpath";
    private static final String DEFAULT_LOCATOR_TYPE = CSS_LOCATOR_TYPE;
    private static final String[] LOCATOR_TYPES = { CSS_LOCATOR_TYPE, XPATH_LOCATOR_TYPE };

    @Autowired
    private WebElementRegistry elementRegistry;

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
                        + "where locator type is optional, supported values are "
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

            String type = DEFAULT_LOCATOR_TYPE;
            if (contains(LOCATOR_TYPES, lastPart)) {
                type = lastPart;
            } else {
                middlePart = Stream.of(middlePart, lastPart)
                    .filter(s -> !isEmpty(s))
                    .collect(joining(SEPARATOR));
            }

            elementRegistry.registerLocator(pageName, middlePart, by(type, value));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private By by(String type, String value) {
        switch (type) {
            case CSS_LOCATOR_TYPE:
                return By.cssSelector(value);
            case XPATH_LOCATOR_TYPE:
                return By.xpath(value);
            default:
                throw new IllegalArgumentException();
        }
    }

    private Properties loadYamlProperties(Resource elementLocatorsResource) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(elementLocatorsResource);
        return yamlPropertiesFactoryBean.getObject();
    }

}
