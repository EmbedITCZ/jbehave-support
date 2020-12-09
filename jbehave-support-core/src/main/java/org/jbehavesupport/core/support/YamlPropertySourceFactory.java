package org.jbehavesupport.core.support;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * The class {@link YamlPropertySourceFactory} allows to use YAML files as {@link org.springframework.core.env.Environment} properties sources.
 * <p>
 * Example usage in spring configuration:
 * <pre>
 * &#064Configuration
 * &#064;PropertySource(value = "common-env.yml", factory = YamlPropertySourceFactory.class)
 * public class TestConfig {
 * }
 * </pre>
 *
 * To use variables please use standard spring ${} placeholder format see {@link PropertySource} for more information
 * For example:
 * <pre>
 * &#064;PropertySource(value = {"common-env.yml", "${spring.profiles.active}.yml"}, factory = YamlPropertySourceFactory.class)
 * </pre>
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(encodedResource.getResource());
        Properties properties = factory.getObject();
        return new PropertiesPropertySource(name == null ? encodedResource.getResource().getFilename() : name, properties);
    }

}
