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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * All types except FAILED are in development, so it's not recommended to use them.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class WebScreenshotCreator {

    public enum Type {
        FAILED(0), DEBUG(3), MANUAL(0), STEP(2), WAIT(1);
        private final int hierarchy;

        Type(int hierarchy) {
            this.hierarchy = hierarchy;
        }

        public int getHierarchy() {
            return hierarchy;
        }
    }

    public static final String FAILED_SCREENSHOTS_KEY = "error_screenshots";
    public static final String REPORT_SCREENSHOTS_KEY = "report_screenshots";
    private static final String FILE_NAME_PATTERN = "%s_%s.png";
    public static final String SCREENSHOTS_DIRECTORY_KEY = "screenshotDirectory";

    /**
     * All types except FAILED are in development, so it's not recommended to use them.
     */
    @Value("${web.screenshot.reporting.mode:MANUAL}")
    private Type desiredMode;

    @Value("${web.screenshot.directory:./target/reports}")
    private String screenshotDirectory;

    private final WebDriver driver;
    private final TestContext testContext;
    private final FileNameResolver fileNameResolver;

    public void createScreenshot(Type screenShotType) {
        if (desiredMode.getHierarchy() >= screenShotType.getHierarchy()) {
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

    private void takesScreenshot(Type screenShotType) throws IOException {
        log.info("Taking {} screenshot will place it in {}", screenShotType, screenshotDirectory);

        prepareDirectory();

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        if (screenshot == null && screenShotType != Type.FAILED) {
            throw new IllegalStateException("Creation of screenshot failed");
        } else if (screenshot != null) {
            File destinationFile = fileNameResolver.resolveFilePath(FILE_NAME_PATTERN, screenshotDirectory, screenShotType.toString()).toFile();
            FileUtils.copyFile(screenshot, destinationFile);

            if (screenShotType == Type.FAILED) {
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
