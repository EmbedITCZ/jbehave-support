package org.jbehavesupport.core.report.extension;

import static java.util.Comparator.comparing;

import java.io.Writer;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.report.ReportRenderingPhase;

import org.jbehavesupport.core.internal.MetadataUtil;

@RequiredArgsConstructor
public class TestContextXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String TEXT_CONTEXT_XML_REPORTER_EXTENSION = "testContext";
    private static final String KEY_VALUE_TAG = "<values><key>%s</key><value><![CDATA[%s]]></value></values>";

    private final TestContext testContext;

    @Override
    public String getName() {
        return TEXT_CONTEXT_XML_REPORTER_EXTENSION;
    }

    @Override
    public Long getPriority() {
        return 10L;
    }

    @Override
    public ReportRenderingPhase getReportRenderingPhase() {
        return ReportRenderingPhase.AFTER_SCENARIO;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        testContext.keySet()
            .stream()
            .filter(key -> testContext.getEntry(key).getMetadata().contains(MetadataUtil.userDefined()))
            .sorted(comparing(String::toLowerCase))
            .forEach(key -> printKeyValue(writer, key, testContext.get(key)));
    }

    private void printKeyValue(final Writer writer, final String key, final Object value) {
        printString(writer, String.format(KEY_VALUE_TAG, key, String.valueOf(value)));
    }
}
