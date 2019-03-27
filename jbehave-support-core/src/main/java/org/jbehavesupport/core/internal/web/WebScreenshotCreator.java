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

    public static final String SCREENSHOTS_KEY = "error_screenshots";
    private static final String FILE_NAME_PATTERN = "FAILED_%s.png";

    @Value("${web.screenshot.directory:./target/reports}")
    private String screenshotDirectory;

    private final WebDriver driver;
    private final TestContext testContext;

    public final void createScreenshot() {
        try {
            if (driver instanceof TakesScreenshot) {
                log.info("Taking error screenshot will place it in {}", screenshotDirectory);
                prepareDirectory();

                File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
                if (screenshot != null) {
                    File destinationFile = getDestinationFile();
                    FileUtils.copyFile(screenshot, destinationFile);
                    storeInTestContext(destinationFile.getName());
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
            final String storyName = testContext.get(AbstractSpringStories.JBEHAVE_SCENARIO, String.class).split("#")[0];
            File destinationFile = new File(screenshotDirectory, String.format(FILE_NAME_PATTERN, storyName));
            int i = 1;
            while (destinationFile.exists()) {
                destinationFile = new File(screenshotDirectory, String.format(FILE_NAME_PATTERN, storyName + "-" + i++));
            }
            return destinationFile;
        } else {
            return new File("screenshot-" + System.currentTimeMillis() + ".png");
        }
    }

    private void storeInTestContext(final String destFile) {
        if (!testContext.contains(SCREENSHOTS_KEY)) {
            testContext.put(SCREENSHOTS_KEY, new LinkedHashSet<String>());
        }
        testContext.get(SCREENSHOTS_KEY, Set.class).add(destFile);
    }

}
