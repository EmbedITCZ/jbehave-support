package org.jbehavesupport.core.internal.web.webdriver;

import java.util.List;

import lombok.Setter;
import org.jbehavesupport.core.web.WebDriverFactory;
import org.jbehavesupport.core.web.WebDriverFactoryResolver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class WebDriverFactoryResolverImpl implements WebDriverFactoryResolver {

    private final List<WebDriverFactory> webDriverFactories;

    @Setter
    @Value("${web.browser:chrome}")
    private String browserName;

    @Override
    public WebDriverFactory resolveWebDriverFactory() {
        return webDriverFactories.stream()
            .filter(c -> c.getName().equals(browserName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No WebDriverFactory found for given browser [" + browserName + "]."));
    }
}
