package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.web.WebScreenshotCreator;
import org.jbehavesupport.core.report.ReportContext;

@RequiredArgsConstructor
public class StepScreenshotsReporterExtension extends AbstractXmlReporterExtension {

    private static final String STEP_SCREENSHOTS_REPORTER_EXTENSION = "stepScreenshots";
    private static final String SCREENSHOT_TAG = "<stepScreenshot>%s</stepScreenshot>" + System.lineSeparator();
    private final TestContext testContext;

    @Override
    public String getName() {
        return STEP_SCREENSHOTS_REPORTER_EXTENSION;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        if (testContext.contains(WebScreenshotCreator.STEP_SCREENSHOTS_KEY)) {
            Set<String> screenshots = testContext.get(WebScreenshotCreator.STEP_SCREENSHOTS_KEY, Set.class);
            for (String screenshotName : screenshots) {
                printScreenshotEntry(writer, screenshotName);
            }
        }
    }

    private void printScreenshotEntry(final Writer writer, String screenshot) {
        printString(writer, String.format(SCREENSHOT_TAG, screenshot));
    }
}
