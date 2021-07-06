package org.jbehavesupport.test.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class SshContainer extends GenericContainer<SshContainer> {

    private static final String IMAGE_VERSION = "panubo/sshd:1.4.0";
    private static SshContainer container;

    public SshContainer() {
        super(IMAGE_VERSION);
    }

    @Override
    public void start() {
        withEnv("SSH_USERS", "sa:1000:1000")
            .withEnv("SSH_ENABLE_PASSWORD_AUTH", "true")
            .withClasspathResourceMapping("ssh/user-setup.sh", "/etc/entrypoint.d/", BindMode.READ_ONLY)
            .withClasspathResourceMapping("ssh/key.pub", "/etc/authorized_keys/sa", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*Server listening on :: port 22.*\\n", 1))
            .withExposedPorts(22);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void updateDynamicPropertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("ssh.hostname", this::getHost);
        registry.add("ssh.port", () -> getMappedPort(22));
        registry.add("ssh.credentials.user", () -> "sa");
        registry.add("ssh.credentials.password", () -> "sa");
        registry.add("ssh.credentials.keyPath", () -> "classpath:/ssh/key");
    }

}
