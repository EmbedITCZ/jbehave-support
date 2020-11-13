package org.jbehavesupport.core.internal.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.openqa.selenium.NoAlertPresentException;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.jbehavesupport.core.web.WebScreenshotType;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebScreenshotCreator {

    public static final String FAILED_SCREENSHOTS_KEY = "error_screenshots";
    public static final String REPORT_SCREENSHOTS_KEY = "report_screenshots";
    private static final String FILE_NAME_PATTERN = "%s_%s.png";
    public static final String SCREENSHOTS_DIRECTORY_KEY = "screenshotDirectory";

    @Value("${web.screenshot.directory:./target/reports}")
    private String screenshotDirectory;

    private final WebDriver driver;
    private final TestContext testContext;
    private final FileNameResolver fileNameResolver;

    public void createScreenshot(WebScreenshotType screenShotType) {
        if (isAlertPresent()) {
            log.warn("Can't take screenshot (alert is present)");
        } else {
            try {
                if (driver instanceof TakesScreenshot) {
                    takesScreenshot(screenShotType);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private boolean isAlertPresent() {
        try {
            if (driver.switchTo() != null) {
                driver.switchTo().alert();
                return true;
            }
        } catch (NoAlertPresentException x) {
            // no op
        }
        return false;
    }

    private void takesScreenshot(WebScreenshotType screenShotType) throws IOException {
        log.info("Taking {} screenshot will place it in {}", screenShotType, screenshotDirectory);
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        if (screenshot == null && screenShotType != WebScreenshotType.FAILED) {
            throw new IllegalStateException("Creation of screenshot failed");
        } else if (screenshot != null) {
            prepareDirectory();
            File destinationFile = fileNameResolver.resolveFilePath(FILE_NAME_PATTERN, screenshotDirectory, screenShotType.toString()).toFile();
            FileUtils.copyFile(screenshot, destinationFile);

            if (screenShotType == WebScreenshotType.FAILED) {
                storeInTestContext(destinationFile.getName(), FAILED_SCREENSHOTS_KEY);
            } else {
                storeInTestContext(destinationFile.getName(), REPORT_SCREENSHOTS_KEY);
            }
        }
    }

    private void prepareDirectory() throws IOException {
        testContext.put(SCREENSHOTS_DIRECTORY_KEY, screenshotDirectory);
        if (!Paths.get(screenshotDirectory).toFile().exists()) {
            Files.createDirectory(Paths.get(screenshotDirectory));
        }
    }

    private void storeInTestContext(final String destFile, String screenshotKey) {
        if (!testContext.contains(screenshotKey)) {
            testContext.put(screenshotKey, new LinkedHashSet<String>());
        }
        testContext.get(screenshotKey, Set.class).add(destFile);
    }
}
