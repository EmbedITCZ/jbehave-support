package org.jbehavesupport.core.report

import groovy.xml.XmlSlurper
import org.apache.commons.io.FileUtils
import org.jbehavesupport.core.report.XmlReporterFactory
import org.jbehavesupport.test.GenericStory
import org.jbehavesupport.test.support.TestSupport
import org.junit.runner.JUnitCore
import org.springframework.test.context.TestContextManager
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class XmlReportIT extends Specification implements TestSupport {
    @Shared runner = new JUnitCore()

    def "Test report index generation"() {
        setup:
        def reportDir = "reportTest"
        def reportDirectory = new File("./target/" + reportDir)
        if (reportDirectory.exists()) {
            FileUtils.cleanDirectory(reportDirectory)
        } else {
            Files.createDirectory(reportDirectory.toPath())
        }
        def testContextManager = new TestContextManager(GenericStory)
        testContextManager.prepareTestInstance(runner)

        def xmlReporterFactory = testContextManager.getTestContext()
            .applicationContext
            .autowireCapableBeanFactory
            .getBean(XmlReporterFactory)

        def reportsDirectoryField = 'reportsDirectory'
        xmlReporterFactory."$reportsDirectoryField" = reportDir

        when:
        def resultSampleContext = runner.run(runWith("report/SampleContext.story"))
        def resultContext = runner.run(runWith("context/Context.story"))

        xmlReporterFactory.destroy()
        def indexFile = new File("./target/${reportDir}/index.xml")
        def indexXsltFile = new File("./target/${reportDir}/index.xslt")
        def reportXsltFile = new File("./target/${reportDir}/report.xslt")

        then:
        resultSampleContext.failureCount == 0
        resultContext.failureCount == 0
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
