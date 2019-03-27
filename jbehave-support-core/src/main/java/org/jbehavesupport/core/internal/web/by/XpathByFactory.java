package org.jbehavesupport.core.internal.web.by;

import org.jbehavesupport.core.web.ByFactory;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

@Component
public class XpathByFactory implements ByFactory {

    @Override
    public By getBy(String value) {
        return By.xpath(value);
    }

    @Override
    public String getType() {
        return "xpath";
    }

}
