package org.jbehavesupport.core.internal.web.webdriver;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.remote.RemoteWebDriver;

import static org.apache.commons.lang3.StringUtils.compareIgnoreCase;

@UtilityClass
public class WebDriverUtil {

    public static boolean isSafari(RemoteWebDriver driver) {
        return compareIgnoreCase("safari", driver.getCapabilities().getBrowserName()) == 0;
    }

}
