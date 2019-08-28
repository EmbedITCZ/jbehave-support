package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.report.ReportRenderingPhase;

import org.jbehavesupport.core.internal.web.WebScreenshotCreator;

@RequiredArgsConstructor
public class FailScreenshotsReporterExtension extends AbstractXmlReporterExtension {

    private static final String FAIL_SCREENSHOTS_REPORTER_EXTENSION = "errorScreenshots";
    private static final String SCREENSHOT_TAG = "<screenshot>%s</screenshot>";
    private static final String RELATIVE_PATH_ADJUSTER = "../../";

    private final TestContext testContext;

    @Override
    public String getName() {
        return FAIL_SCREENSHOTS_REPORTER_EXTENSION;
    }

    @Override
    public Long getPriority() {
        return -1L;
    }

    @Override
    public ReportRenderingPhase getReportRenderingPhase() {
        return ReportRenderingPhase.AFTER_SCENARIO;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        if (testContext.contains(WebScreenshotCreator.FAILED_SCREENSHOTS_KEY)) {
            Set<String> screenshots = testContext.get(WebScreenshotCreator.FAILED_SCREENSHOTS_KEY, Set.class);
            String screenshotDirectory = testContext.get(WebScreenshotCreator.SCREENSHOTS_DIRECTORY_KEY, String.class);
            for (String screenshotName : screenshots) {
                Path path = Paths.get(screenshotDirectory, screenshotName);
                String pathString = path.isAbsolute() ? path.toString() : RELATIVE_PATH_ADJUSTER + path.toString();
                printScreenshotEntry(writer, pathString);
            }
        }
    }

    private void printScreenshotEntry(final Writer writer, String screenshot) {
        printString(writer, String.format(SCREENSHOT_TAG, screenshot));
    }
}
