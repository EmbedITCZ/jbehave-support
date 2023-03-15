package org.jbehavesupport.test.support;

import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.jbehave.core.annotations.Given;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Example steps to fill Ssh log.
 */
@Component
@RequiredArgsConstructor
public final class SshGeneratorSteps {

    private final Environment environment;

    @Given("ssh test data are filled")
    public void fillTestData() throws IOException {
        try (SSHClient sshClient = new SSHClient()) {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(environment.getProperty("ssh.hostname"), environment.getProperty("ssh.port", Integer.class));
            sshClient.authPassword(environment.getProperty("ssh.credentials.user"), environment.getProperty("ssh.credentials.password"));

            ZonedDateTime startTime = ZonedDateTime.now();
            String timestampFormat = environment.getProperty("ssh.timestampFormat");
            String timestamp = startTime.withZoneSameInstant(ZoneId.of("GMT")).format(DateTimeFormatter.ofPattern(timestampFormat));
            String logText = "some long string containing cdata in many Cdata forms." +
                "Such as correct one <![CDATA[1832300759061]]> and malformed <![[1832300759061]]> " +
                "and incomplete <![CDATA[ and duplicated correct one <![CDATA[1832300759061]]> with some additional information" +
                "Also some unexpected closing like ] and ]] also sharp ]]>";
            String command = "echo " + timestamp + " " + "\"" + logText + "\"" + " > " + environment.getProperty("ssh.logPath");
            sshClient.startSession().exec(command);
        }
    }

}
