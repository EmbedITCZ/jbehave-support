package org.jbehavesupport.core.report

import groovy.xml.XmlSlurper
import org.apache.commons.io.FileUtils
import org.jbehavesupport.test.GenericStory
import org.jbehavesupport.test.support.TestSupport
import org.springframework.test.context.TestContextManager
import spock.lang.Specification

import java.nio.file.Files

class XmlReportIT extends Specification implements TestSupport {

    def "Test report index generation"() {
        setup:
        def reportDir = "reportTest"
        def reportDirectory = new File("./target/" + reportDir)
        if (reportDirectory.exists()) {
            FileUtils.cleanDirectory(reportDirectory)
        } else {
            Files.createDirectory(reportDirectory.toPath())
        }

        def xmlReporterFactory = new TestContextManager(GenericStory).getTestContext()
            .applicationContext
            .autowireCapableBeanFactory
            .getBean(XmlReporterFactory)

        def reportsDirectoryField = 'reportsDirectory'
        xmlReporterFactory."$reportsDirectoryField" = reportDir

        when:
        def resultSampleContext = run(runWith("report/SampleContext.story"))
        def resultContext = run(runWith("context/Context.story"))

        xmlReporterFactory.destroy()
        def indexFile = new File("./target/${reportDir}/index.xml")
        def indexXsltFile = new File("./target/${reportDir}/index.xslt")
        def reportXsltFile = new File("./target/${reportDir}/report.xslt")

        then:
        resultSampleContext.totalFailureCount == 0
        resultContext.totalFailureCount == 0
        indexFile.exists()
        indexFile.length() > 0
        indexXsltFile.exists()
        indexXsltFile.length() > 0
        reportXsltFile.exists()
        reportXsltFile.length() > 0

        def index = new XmlSlurper().parse(indexFile)
        index.item[0].fileName == "Context.xml"
        index.item[1].fileName == "SampleContext.xml"

        cleanup:
        xmlReporterFactory."$reportsDirectoryField" = "reports"
    }

}
