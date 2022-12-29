package org.jbehavesupport.core.web;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.openqa.selenium.WebDriver;

import java.util.function.Consumer;

/**
 * This class holds settings for WEB steps.
 * The test configuration has to provide this bean with application qualifier.
 * <p>
 * Example:
 * <pre>
 * &#064;Configuration
 * public class MyTestConfiguration {
 *
 *     &#064;Bean
 *     &#064;Qualifier("MYAPP")
 *     public WebSetting myAppWebSetting() {
 *         return WebSetting.builder()
 *                  .homePageUrl("http://localhost:8080/myapp")
 *                  .htmlRenderer(WebTableSteps.HtmlRenderer.SIMPLE)
 *                  .elementLocatorsSource("ui-mapping/*.yml")
 *                  .build();
 *     }
 *
 * }
 *
 * </pre>
 */
@Value
@Builder
public class WebSetting {

    private String homePageUrl;
    @Singular
    private List<String> elementLocatorsSources;
    @Builder.Default
    private Consumer<WebDriver> waitForLoad = webDriver -> {
    };

}
