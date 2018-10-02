package org.jbehavesupport.core.report;

import java.io.Writer;

public interface XmlReporterExtension {

    String getName();

    Long getPriority();

    default ReportRenderingPhase getReportRenderingPhase()  {
        return ReportRenderingPhase.AFTER_STORY;
    }

    void print(Writer writer, ReportContext reportContext);
}
