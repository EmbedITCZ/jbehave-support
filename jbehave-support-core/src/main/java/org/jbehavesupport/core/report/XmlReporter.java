package org.jbehavesupport.core.report;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.XmlOutput;
import org.jbehave.core.steps.Timing;

public class XmlReporter extends XmlOutput {

    private static final String XML_STYLESHEET_HEADER = "<?xml-stylesheet type=\"text/xsl\" href=\"%s\"?>";

    private static final String EXECUTION_START = "startExecution";
    private static final String EXECUTION_END = "endExecution";
    private static final String EXECUTION_DURATION = "duration";
    private static final String EXECUTION_STATUS = "status";
    private static final String DEFAULT_PATTERN = "{0}\n";
    private static final String SCENARIO_REPORT = "scenarioReportExtensions";

    public static final String TAG_BEGIN = "<%s>";
    public static final String TAG_END = "</%s>";
    public static final String TAG_SELF_CLOSE = ("<%s/>");
    public static final String TAG_CDATA = "<![CDATA[%s]]>";

    private final List<XmlReporterExtension> extensions;
    private final Writer writer;
    private final ReportContext reportContext;
    private final String templateName;

    public XmlReporter(final List<XmlReporterExtension> extensions, final PrintStream printStream, final String templateName) {
        super(printStream, defaultPatterns());
        this.writer = new OutputStreamWriter(printStream);
        this.extensions = extensions.stream().sorted(Comparator.comparing(XmlReporterExtension::getPriority)).collect(Collectors.toList());
        this.reportContext = new ReportContext();
        this.templateName = templateName;
    }

    private void println(String... args) {
        for (String arg : args) {
            print(arg);
            print(System.lineSeparator());
        }
    }

    @Override
    public void beforeStory(final Story story, final boolean givenStory) {
        if (isNull(reportContext.endExecution())) {
            reportContext.startExecution(currentTimeMillis());
            if (nonNull(templateName)) {
                println(String.format(XML_STYLESHEET_HEADER, templateName));
            }
        }
        super.beforeStory(story, givenStory);
    }

    @Override
    public void failed(final String step, final Throwable storyFailure) {
        reportContext.statusFail();
        super.failed(step, storyFailure);
    }

    @Override
    public void afterScenario(Timing timing) {
        super.afterScenario(timing);
        printTag(TAG_BEGIN, SCENARIO_REPORT);
        writeExtensions(e -> ReportRenderingPhase.AFTER_SCENARIO.equals(e.getReportRenderingPhase()));
        printTag(TAG_END, SCENARIO_REPORT);
    }

    @Override
    public void afterStory(final boolean givenOrRestartingStory) {
        if (!givenOrRestartingStory) {
            reportContext.endExecution(currentTimeMillis());
            printReportData();
            writeExtensions(e -> ReportRenderingPhase.AFTER_STORY.equals(e.getReportRenderingPhase()));
        }

        super.afterStory(givenOrRestartingStory);
    }

    private void printReportData() {
        println(format(EXECUTION_START, DEFAULT_PATTERN, reportContext.startExecutionZoned()));
        println(format(EXECUTION_END, DEFAULT_PATTERN, reportContext.endExecutionZoned()));
        println(format(EXECUTION_DURATION, DEFAULT_PATTERN, reportContext.duration()));
        println(format(EXECUTION_STATUS, DEFAULT_PATTERN, reportContext.status().name()));
    }

    private void writeExtensions(Predicate<? super XmlReporterExtension> filter) {
        extensions.stream()
            .filter(filter)
            .forEach(e -> {
                try {
                    printTag(TAG_BEGIN, e.getName());
                    e.print(writer, reportContext);
                    printTag(TAG_END, e.getName());
                    writer.flush();
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            });
    }

    private static Properties defaultPatterns() {
        Properties properties = new Properties();
        properties.put(EXECUTION_START, "<startExecution>{0}</startExecution>");
        properties.put(EXECUTION_END, "<endExecution>{0}</endExecution>");
        properties.put(EXECUTION_DURATION, "<duration>{0}</duration>");
        properties.put(EXECUTION_STATUS, "<status>{0}</status>");
        return properties;
    }

    private void printTag(String tagFormat, String tagName) {
        println(String.format(tagFormat, tagName));
    }

}
