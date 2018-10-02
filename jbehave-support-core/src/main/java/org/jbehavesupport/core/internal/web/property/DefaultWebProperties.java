package org.jbehavesupport.core.internal.web.property;

import org.jbehavesupport.core.web.WebPropertyContext;
import org.jbehavesupport.core.web.WebProperty;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultWebProperties {

    @Bean
    public WebProperty<Boolean> enabledWebProperty() {
        return new SimpleWebProperty<>("ENABLED", (e) -> e.isEnabled());
    }

    @Bean
    public WebProperty<Boolean> selectedWebProperty() {
        return new SimpleWebProperty<>("SELECTED", (e) -> e.isSelected());
    }

    @Bean
    public WebProperty<String> textWebProperty() {
        return new SimpleWebProperty<>("TEXT", (e) -> e.getText());
    }

    @Bean
    public WebProperty<String> classWebProperty() {
        return new SimpleWebProperty<>("CLASS", (e) -> e.getAttribute("class"));
    }

    @Bean
    public WebProperty<String> valueWebProperty() {
        return new SimpleWebProperty<>("VALUE", (e) -> e.getAttribute("value"));
    }

    @Bean
    public WebProperty<Boolean> editableWebProperty() {
        return new SimpleWebProperty<>("EDITABLE", (e) -> {
            String readonly = e.getAttribute("readonly");
            return e.isEnabled() && (readonly == null || "false".equals(readonly));
        });
    }

    @Bean
    public WebProperty<String> selectedTextWebProperty() {
        return new SimpleWebProperty<>("SELECTED_TEXT",
            (e) -> e.findElements(By.tagName("option"))
                .stream()
                .filter((o) -> o.getAttribute("selected") != null && o.getAttribute("selected").equals("true"))
                .map((o) -> o.getText())
                .findFirst()
                .orElseThrow(IllegalArgumentException::new));
    }

    @Bean
    public WebProperty<Integer> rowCountWebProperty() {
        return new SimpleWebProperty<>("ROW_COUNT",
            (e) -> e.findElement(By.tagName("tbody"))
                .findElements(By.tagName("tr"))
                .size());
    }

    @Bean
    public WebProperty<Boolean> displayedWebProperty() {
        return new AbstractWebProperty<Boolean>() {
            @Override
            public String name() {
                return "DISPLAYED";
            }
            @Override
            public Boolean value(WebPropertyContext ctx) {
                try {
                    return findElement(ctx).isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            }
        };
    }

}
