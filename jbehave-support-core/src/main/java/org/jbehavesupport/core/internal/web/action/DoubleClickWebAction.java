package org.jbehavesupport.core.internal.web.action;

import org.jbehavesupport.core.web.WebActionContext;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import static org.jbehavesupport.core.internal.web.webdriver.WebDriverUtil.isSafari;

@Component
public class DoubleClickWebAction extends AbstractWebAction {

    @Override
    public String name() {
        return "DOUBLE_CLICK";
    }

    @Override
    public void perform(WebActionContext ctx) {
        WebElement element = findElement(ctx);

        scrollIntoView(element);

        // safari does not support double click from selenium correctly in v13
        if (driver instanceof JavascriptExecutor && isSafari((RemoteWebDriver) driver)) {
            String doubleClickScript = "var event = document.createEvent('MouseEvents');" +
                "event.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);" +
                "arguments[0].dispatchEvent(event);";
            ((JavascriptExecutor) driver).executeScript(doubleClickScript, element);
        } else {
            new Actions(driver)
                .doubleClick(element)
                .perform();
        }
    }

}
