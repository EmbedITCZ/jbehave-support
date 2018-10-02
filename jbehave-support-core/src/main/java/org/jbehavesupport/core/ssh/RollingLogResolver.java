package org.jbehavesupport.core.ssh;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

public interface RollingLogResolver {
    List<String> resolveLogNames(String configuredLogPath, SshTemplate sshTemplate, ZonedDateTime startTime, ZonedDateTime endTime) throws IOException;
}
