package org.jbehavesupport.core.report.extension;

import static org.apache.commons.text.StringEscapeUtils.escapeXml11;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.jbehavesupport.core.report.XmlReporterExtension;

import org.apache.commons.collections4.MapUtils;
import org.jbehavesupport.core.report.XmlReporter;

public abstract class AbstractXmlReporterExtension implements XmlReporterExtension {


    private static final Long DEFAULT_PRIORITY = 0L;

    @Override
    public Long getPriority() {
        return DEFAULT_PRIORITY;
    }

    protected void printEnd(final Writer writer, final String tag) {
        printString(writer, String.format(XmlReporter.TAG_END, StringEscapeUtils.escapeXml10(tag)));
    }

    protected void printBegin(final Writer writer, final String tag) {
        printBegin(writer, tag, null);
    }

    protected void printBegin(final Writer writer, final String tag, final Map<String, String> attributes) {
        printTag(writer, tag, XmlReporter.TAG_BEGIN, attributes);
    }

    protected void printSelfClosed(final Writer writer, final String tag, final Map<String, String> attributes) {
        printTag(writer, tag, XmlReporter.TAG_SELF_CLOSE, attributes);
    }

    private void printTag(Writer writer, String tag, String tagFormat, Map<String, String> attributes) {
        String newTag = StringEscapeUtils.escapeXml10(tag);
        if (!MapUtils.isEmpty(attributes)) {
            newTag += " " + attributes.entrySet().stream()
                .map(entry -> StringEscapeUtils.escapeXml10(entry.getKey()) + "=\"" + StringEscapeUtils.escapeXml10(entry.getValue()) + "\"")
                .collect(Collectors.joining(" "));
        }
        printString(writer, String.format(tagFormat, newTag));
    }

    protected void printCData(final Writer writer, final String string) {
        printString(writer, String.format(XmlReporter.TAG_CDATA, escapeXml11(string)));
    }

    protected void printString(final Writer writer, final String string) {
        try {
            writer.write(string);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
