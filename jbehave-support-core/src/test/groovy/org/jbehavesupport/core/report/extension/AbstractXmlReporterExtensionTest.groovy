package org.jbehavesupport.core.report.extension

import org.jbehavesupport.core.report.ReportContext
import spock.lang.Specification
import spock.lang.Unroll

class AbstractXmlReporterExtensionTest extends Specification {

    @Unroll
    "test printBegin(writer, #tag, #attributes)"() {
        given:
        def extension = new AbstractXmlReporterExtension() {
            @Override
            String getName() {
                return "ext"
            }

            @Override
            void print(final Writer writer, final ReportContext reportContext) {
            }
        }
        def writer = new StringWriter()

        when:
        extension.printBegin(writer, tag, attributes)

        then:
        result == writer.toString()

        where:
        tag   | attributes                            || result
        "tag" | null                                  || "<tag>"
        "tag" | ['time': '10:20', 'date': '1.1.2011'] || "<tag time=\"10:20\" date=\"1.1.2011\">"
    }
}
