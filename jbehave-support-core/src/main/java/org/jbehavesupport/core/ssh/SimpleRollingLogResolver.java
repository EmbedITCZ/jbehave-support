package org.jbehavesupport.core.ssh;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class SimpleRollingLogResolver implements RollingLogResolver {
    @Override
    public List<String> resolveLogNames(final String configuredLogPath, final SshTemplate sshTemplate, final ZonedDateTime startTime,
        final ZonedDateTime endTime) throws IOException {
        return Arrays.asList(configuredLogPath);
    }
}
