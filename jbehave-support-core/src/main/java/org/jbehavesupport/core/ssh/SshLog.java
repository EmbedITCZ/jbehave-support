package org.jbehavesupport.core.ssh;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SshLog {
    private String logContents;
    private SshSetting sshSetting;
}
