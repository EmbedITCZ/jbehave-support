package org.jbehavesupport.core.internal.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
            try {
                if (ExpectedConditions.alertIsPresent().apply(driver) != null) {
                    log.info("Can't take screenshot (alert is present)");
                } else if (driver instanceof TakesScreenshot) {
                    takesScreenshot(screenShotType);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private void takesScreenshot(Type screenShotType) throws IOException {
        log.info("Taking {} screenshot will place it in {}", screenShotType, screenshotDirectory);

        prepareDirectory();

        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        if (screenshot == null && screenShotType != Type.FAILED) {
            throw new IllegalStateException("Creation of screenshot failed");
        } else if (screenshot != null) {
            File destinationFile = getDestinationFile(screenShotType);
            FileUtils.copyFile(screenshot, destinationFile);

            if (screenShotType == Type.FAILED) {
                storeInTestContext(destinationFile.getName(), FAILED_SCREENSHOTS_KEY);
            } else {
                storeInTestContext(destinationFile.getName(), REPORT_SCREENSHOTS_KEY);
            }
        }
    }

    private void prepareDirectory() throws IOException {
        if (!Paths.get(screenshotDirectory).toFile().exists()) {
            Files.createDirectory(Paths.get(screenshotDirectory));
            testContext.put("screenshotDirectory", screenshotDirectory);
        }
    }

    private File getDestinationFile(Type screenShotType) {
        return fileNameResolver.resolveFilePath(FILE_NAME_PATTERN, screenshotDirectory, screenShotType.toString()).toFile();
    }

    private void storeInTestContext(final String destFile, String screenshotKey) {
        if (!testContext.contains(screenshotKey)) {
            testContext.put(screenshotKey, new LinkedHashSet<String>());
        }
        testContext.get(screenshotKey, Set.class).add(destFile);
    }
}
