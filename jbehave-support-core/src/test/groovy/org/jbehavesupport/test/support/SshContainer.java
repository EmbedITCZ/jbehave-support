package org.jbehavesupport.test.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;

public class SshContainer extends GenericContainer<SshContainer> {

    private static final String IMAGE_VERSION = "testcontainers/sshd:1.0.0";

    public SshContainer() {
        super(IMAGE_VERSION);
    }

    @Override
    public void start() {
        withExposedPorts(22)
            .withClasspathResourceMapping("ssh/key.pub", "/etc/authorized_keys/root", BindMode.READ_ONLY)
            .withCommand(
                "sh",
                "-c",
                // Disable ipv6 & Make it listen on all interfaces, not just localhost
                "echo \"root:sa\" | chpasswd && /usr/sbin/sshd -D -o PermitRootLogin=yes -o AddressFamily=inet -o GatewayPorts=yes"
            );
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void updateDynamicPropertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("ssh.hostname", this::getHost);
        registry.add("ssh.port", () -> getMappedPort(22));
        registry.add("ssh.credentials.user", () -> "root");
        registry.add("ssh.credentials.password", () -> "sa");
        registry.add("ssh.credentials.keyPath", () -> "classpath:/ssh/key");
    }

}
