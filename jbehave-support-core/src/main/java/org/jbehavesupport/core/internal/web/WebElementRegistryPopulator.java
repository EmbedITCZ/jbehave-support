package org.jbehavesupport.core.internal.web;

import static org.apache.commons.lang3.StringUtils.endsWith;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.WebSetting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebElementRegistryPopulator {

    private static final String YAML_EXTENSION = ".yaml";

    private final ResourcePatternResolver resourcePatternResolver;
    private final YamlElementLocatorParser yamlElementLocatorParser;
    @Autowired(required = false)
    private List<WebSetting> settings;

    @PostConstruct
    public void initRegistry() throws IOException {
        if (settings != null) {
            for (WebSetting setting : settings) {
                for (String source : setting.getElementLocatorsSources()) {
                    handleResourcePattern(source);
                }
            }
        }
    }

    private void handleResourcePattern(String source) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources(source);
        for (Resource resource : resources) {
            handleResource(resource);
        }
    }

    private void handleResource(Resource resource) {
        if (endsWith(resource.getFilename(), YAML_EXTENSION)) {
            yamlElementLocatorParser.process(resource);
        } else {
            throw new IllegalArgumentException(
                "Given element locators source [" + resource.getFilename() + "] is unsupported, please use yaml file.");
        }
    }

}
