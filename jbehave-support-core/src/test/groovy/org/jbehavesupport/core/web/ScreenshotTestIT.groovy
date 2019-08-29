package org.jbehavesupport.core.web

import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.web.WebScreenshotCreator
import org.openqa.selenium.WebDriver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class ScreenshotTestIT extends Specification {

    @Autowired
    WebDriver driver

    @Autowired
    WebScreenshotCreator webScreenshotCreator

    void takesScreenshot() {
        when:

        driver.get("https://google.com")
        webScreenshotCreator.createScreenshot(WebScreenshotCreator.Type.MANUAL)

        then:
        File screenshotDirectory = new File("./target/reports")
        FilenameFilter filter = new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.matches("MANUAL_[0-9]+\\.png")
            }
        }
        screenshotDirectory.listFiles(filter).length > 0
    }
}
