package org.jbehavesupport.test.support;

import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class SshContainer extends FixedHostPortGenericContainer<SshContainer> {

    private static final String IMAGE_VERSION = "panubo/sshd:latest";
    private static SshContainer container;

    private SshContainer() {
        super(IMAGE_VERSION);
    }

    public static SshContainer getInstance() {
        if (container == null) {
            container = new SshContainer()
            .withEnv("SSH_USERS", "sa:1000:1000")
            .withEnv("SSH_ENABLE_PASSWORD_AUTH", "true")
            .withClasspathResourceMapping("ssh/user-setup.sh", "/etc/entrypoint.d/", BindMode.READ_ONLY)
            .withClasspathResourceMapping("ssh/key.pub", "/etc/authorized_keys/sa", BindMode.READ_ONLY)
            .waitingFor(Wait.forLogMessage(".*Server listening on :: port 22.*\\n", 1))
            .withFixedExposedPort(2000, 22);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        // do nothing, Ryuk handles shut down with JVM
    }

}
