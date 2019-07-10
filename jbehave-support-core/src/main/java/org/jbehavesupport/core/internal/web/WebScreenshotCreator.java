package org.jbehavesupport.core.internal.web;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jbehavesupport.core.TestContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.plexus.util.FileUtils;
import org.jbehavesupport.core.AbstractSpringStories;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebScreenshotCreator {

    public static final String FAILED_SCREENSHOTS_KEY = "error_screenshots";
    public static final String STEP_SCREENSHOTS_KEY = "step_screenshots";
    public static final String FAILED = "FAILED";
    public static final String STEP_SCREENSHOT = "STEP_SCREENSHOT";
    private static final String FILE_NAME_PATTERN = "%s_%s.png";

    @Value("${web.screenshot.directory:./target/reports}")
    private String screenshotDirectory;
    private String screenShotType;

    private final WebDriver driver;
    private final TestContext testContext;

    public final void createScreenshot(String screenShotType) {
        this.screenShotType = screenShotType;
        try {
            if (driver instanceof TakesScreenshot) {
                log.info("Taking {} screenshot will place it in {}", screenShotType.equals(FAILED) ? "error" : "step", screenshotDirectory);
                prepareDirectory();

                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                if (screenshot != null) {
                    File destinationFile = getDestinationFile();
                    FileUtils.copyFile(screenshot, destinationFile);
                    switch (screenShotType) {
                        case FAILED: {
                            storeFailedInTestContext(destinationFile.getName());
                            break;
                        }

                        case STEP_SCREENSHOT: {
                            storeStepInTestContext(destinationFile.getName());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void prepareDirectory() throws IOException {
        if (!Paths.get(screenshotDirectory).toFile().exists()) {
            Files.createDirectory(Paths.get(screenshotDirectory));
            testContext.put("screenshotDirectory", screenshotDirectory);
        }
    }

    private File getDestinationFile() {
        if (testContext.contains(AbstractSpringStories.JBEHAVE_SCENARIO)) {
            String storyName = testContext.get(AbstractSpringStories.JBEHAVE_SCENARIO, String.class).split("#")[0];
            File destinationFile = new File(screenshotDirectory, String.format(FILE_NAME_PATTERN, screenShotType, storyName));
            int i = 1;
            while (destinationFile.exists()) {
                destinationFile = new File(screenshotDirectory, String.format(FILE_NAME_PATTERN, screenShotType, storyName + "-" + i++));
            }
            return destinationFile;
        } else {
            return new File("screenshot-" + System.currentTimeMillis() + ".png");
        }
    }

    private void storeFailedInTestContext(final String destFile) {
        if (!testContext.contains(FAILED_SCREENSHOTS_KEY)) {
            testContext.put(FAILED_SCREENSHOTS_KEY, new LinkedHashSet<String>());
        }
        testContext.get(FAILED_SCREENSHOTS_KEY, Set.class).add(destFile);
    }

    private void storeStepInTestContext(final String destFile) {
        if (!testContext.contains(STEP_SCREENSHOTS_KEY)) {
            testContext.put(STEP_SCREENSHOTS_KEY, new LinkedHashSet<String>());
        }
        testContext.get(STEP_SCREENSHOTS_KEY, Set.class).add(destFile);
    }

}
