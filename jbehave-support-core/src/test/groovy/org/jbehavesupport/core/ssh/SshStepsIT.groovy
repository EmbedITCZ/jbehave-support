package org.jbehavesupport.core.ssh

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.TestContext
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter
import org.jbehavesupport.test.support.SshContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@ContextConfiguration(classes = TestConfig)
class SshStepsIT extends Specification {

    @Shared
    static SshContainer sshContainer = new SshContainer()

    @Autowired
    SshSteps sshSteps

    @Autowired
    TestContext testContext

    @Autowired
    Environment environment

    @DynamicPropertySource
    static void sshProperties(DynamicPropertyRegistry registry) {
        sshContainer.updateDynamicPropertyRegistry(registry)
    }

    def setupSpec() {
        sshContainer.start()
    }

    def cleanupSpec() {
        sshContainer.stop()
    }

    def "test soft assertions in logContainsData"() {
        given:

        def table = "| header               | \n" +
                    "| invalidValue         | \n" +
                    "| anotherInvalidValue  | "

        def startTime = ZonedDateTime.now().minusMinutes(5)
        sshSteps.saveLogStartTimeOnSaved(new ExpressionEvaluatingParameter<String>(startTime.toString()))
        def timestampFormat = environment.getProperty("ssh.timestampFormat")
        def sshClient = getSshClient()
        def timestamp = ZonedDateTime.now().minusMinutes(1).withZoneSameInstant(ZoneId.of("GMT")).format(DateTimeFormatter.ofPattern(timestampFormat))
        def logText = "some long string containing cdata in many Cdata forms." +
            "Such as correct one <![CDATA[1832300759061]]> and malformed <![[1832300759061]]> " +
            "and incomplete <![CDATA[ and duplicated correct one <![CDATA[1832300759061]]> with some additional information" +
            "Also some unexpected closing like ] and ]] also sharp ]]>"
        def command = "echo " + timestamp + " " + "\"" + logText + "\"" + " > " + environment.getProperty("ssh.logPath")
        sshClient.startSession().exec(command)

        when:
        sshSteps.logContainsData("TEST", table)

        then:
        def exception = thrown(AssertionError)
        exception.getMessage().contains("Multiple Failures (2 failures)")
    }

    private SSHClient getSshClient() throws IOException {
        SSHClient sshClient = new SSHClient()
        sshClient.addHostKeyVerifier(new PromiscuousVerifier())
        sshClient.connect(environment.getProperty("ssh.hostname"), environment.getProperty("ssh.port", Integer.class))
        sshClient.authPassword(environment.getProperty("ssh.credentials.user"), environment.getProperty("ssh.credentials.password"))
        return sshClient
    }
}
