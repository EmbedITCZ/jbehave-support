package org.jbehavesupport.core.internal.web.property;

import org.jbehavesupport.core.web.WebElementLocator;
import org.jbehavesupport.core.web.WebProperty;
import org.jbehavesupport.core.web.WebPropertyContext;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractWebProperty<T> implements WebProperty<T> {

    @Autowired
    private WebElementLocator elementLocator;

    protected final WebElement findElement(WebPropertyContext ctx) {
        return elementLocator.findElement(ctx.getPage(), ctx.getElement());
    }

}
