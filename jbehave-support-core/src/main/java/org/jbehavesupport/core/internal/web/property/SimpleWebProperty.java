package org.jbehavesupport.core.internal.web.property;

import java.util.function.Function;

import org.jbehavesupport.core.web.WebPropertyContext;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class SimpleWebProperty<T> extends AbstractWebProperty<T> {

    private final String name;
    private final Function<WebElement, T> provider;

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final T value(WebPropertyContext ctx) {
        WebElement element = findElement(ctx);
        return provider.apply(element);
    }

}
