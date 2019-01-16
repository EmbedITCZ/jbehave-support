package org.jbehavesupport.core.support;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * The class {@link YamlPropertiesConfigurer} allows to use YAML files as {@link Environment} properties sources.
 * <p>
 * Example usage in spring configuration:
 * <pre>
 * &#064;Bean
 * public static YamlPropertiesConfigurer yamlPropertiesConfigurer() {
 *     return new YamlPropertiesConfigurer("common-env.yml", "{profile}.yml");
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class YamlPropertiesConfigurer implements BeanFactoryPostProcessor, EnvironmentAware, ResourceLoaderAware, PriorityOrdered {

    private static final String PROFILE_PLACEHOLDER = "{profile}";

    private String[] locations;
    private Environment environment;
    private ResourceLoader resourceLoader;
    private int order = HIGHEST_PRECEDENCE;

    public YamlPropertiesConfigurer(String... locations) {
        this.locations = locations;
    }

    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        requireNonNull(locations);
        requireNonNull(environment);
        requireNonNull(resourceLoader);
        configureYamlPropertiesSource();
    }

    private void configureYamlPropertiesSource() {
        PropertySource yamlPropertiesSource = resolveYamlPropertiesSource();
        ((ConfigurableEnvironment) environment).getPropertySources().addLast(yamlPropertiesSource);
    }

    private PropertySource resolveYamlPropertiesSource() {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resolveResources());
        return new PropertiesPropertySource("yaml", yamlPropertiesFactoryBean.getObject());
    }

    private Resource[] resolveResources() {
        return Stream.of(locations)
            .map(this::resolveLocation)
            .flatMap(Function.identity())
            .map(location -> resourceLoader.getResource(location))
            .toArray(Resource[]::new);
    }

    private Stream<String> resolveLocation(String location) {
        if (location.contains(PROFILE_PLACEHOLDER)) {
            List<String> resolvedLocations = Stream.of(environment.getActiveProfiles())
                .map(profile -> location.replace(PROFILE_PLACEHOLDER, profile))
                .filter(resolvedLocation -> resourceLoader.getResource(resolvedLocation).exists())
                .collect(toList());
            if (resolvedLocations.isEmpty()) {
                throw new IllegalArgumentException(
                    "Can not find any resource for given location[" + location + "] and current active profiles " + Arrays.toString(environment.getActiveProfiles()));
            }
            return resolvedLocations.stream();
        }
        return Stream.of(location);
    }

}
