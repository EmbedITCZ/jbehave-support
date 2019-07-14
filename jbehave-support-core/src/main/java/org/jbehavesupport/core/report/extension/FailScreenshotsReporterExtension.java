package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.util.Set;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.report.ReportRenderingPhase;

import org.jbehavesupport.core.internal.web.WebScreenshotCreator;
import org.springframework.beans.factory.annotation.Autowired;

public class FailScreenshotsReporterExtension extends AbstractXmlReporterExtension {

    private static final String FAIL_SCREENSHOTS_REPORTER_EXTENSION = "errorScreenshots";
    private static final String SCREENSHOT_TAG = "<screenshot>%s</screenshot>";

    @Autowired
    private TestContext testContext;

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
            for (String screenshotName : screenshots) {
                printScreenshotEntry(writer, screenshotName);
            }
        }
    }

    private void printScreenshotEntry(final Writer writer, String screenshot) {
        printString(writer, String.format(SCREENSHOT_TAG, screenshot));
    }
}
