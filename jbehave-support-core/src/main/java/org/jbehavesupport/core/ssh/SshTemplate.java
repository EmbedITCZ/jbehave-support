package org.jbehavesupport.core.ssh;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.springframework.util.StringUtils;

public class SshTemplate {
    private String timestampFormat;
    private SshSetting sshSetting;

    private RollingLogResolver rollingLogResolver;
    private SSHClient sshClient;

    private interface Commands {
        String TIMEZONE = "timedatectl | grep zone | grep -Po '(.*zone: )\\K(\\S+)'";
        String EPOCH_TIME = "date +%s";
        String FIRST_LAST_LINES_BETWEEN_TIMESTAMPS = "awk '{if ($1\" \"$2 >= \"%s\")  p=1; if ($1\" \"$2 >= \"%s\")  p=0;} p { print NR }' %s | sed -n '1p;$p'";
        String PRINT_BETWEEN_LINES = "awk 'NR >= %d && NR <= %d' %s";
    }

    private interface Constants {
        String CMD_DELIMITER = "; ";
    }

    public SshTemplate(SshSetting sshSetting, String timestampFormat, RollingLogResolver rollingLogResolver) {
        this.sshSetting = sshSetting;
        this.timestampFormat = timestampFormat;
        this.rollingLogResolver = rollingLogResolver;
    }

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public SshSetting getSshSetting() {
        return sshSetting;
    }

    public SshLog copyLog(ZonedDateTime startTime, ZonedDateTime endTime) throws IOException {
        notNull(startTime, "startTime cannot be null");
        notNull(endTime, "endTime cannot be null");
        isTrue(!StringUtils.isEmpty(sshSetting.getLogPath()), "logPath must not be null or empty");
        isTrue(!StringUtils.isEmpty(timestampFormat), "timestampFormat of log must not be null or empty");
        // if server has different time (ntp out of sync etc.)
        Long serverEpochTime = Long.valueOf(executeCommand(Commands.EPOCH_TIME).trim());
        Long timeOffset = serverEpochTime - ZonedDateTime.now().toEpochSecond();

        // if server has different time zone
        String timeZone = executeCommand(Commands.TIMEZONE).trim();
        ZonedDateTime serverStartTime = startTime.withZoneSameInstant(ZoneId.of(timeZone)).plusSeconds(timeOffset);
        ZonedDateTime serverEndTime = endTime.withZoneSameInstant(ZoneId.of(timeZone)).plusSeconds(timeOffset);

        List<String> logPaths = rollingLogResolver.resolveLogNames(sshSetting.getLogPath(), this, serverStartTime, serverEndTime);
        StringBuilder logs = new StringBuilder();
        for (String logPath : logPaths) {
            logs.append(copySingleLogFile(serverStartTime, serverEndTime, logPath));
        }

        return new SshLog(logs.toString(), sshSetting);
    }

    private String copySingleLogFile(final ZonedDateTime startTime, final ZonedDateTime endTime, final String logPath) throws IOException {
        // get first and last line number
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timestampFormat);
        String firstLastLineCommand = String
            .format(Commands.FIRST_LAST_LINES_BETWEEN_TIMESTAMPS, startTime.format(formatter), endTime.format(formatter), logPath);
        String firstLastLines = executeCommand(firstLastLineCommand).trim();

        String logContents = "";
        if (!firstLastLines.isEmpty()) {
            String[] lineNumbers = firstLastLines.split("\\s+");
            Long startLine = Long.valueOf(lineNumbers[0]);
            Long endLine = Long.valueOf(lineNumbers[1]);

            // get lines between line numbers
            String getLinesCommand = String.format(Commands.PRINT_BETWEEN_LINES, startLine, endLine, logPath);
            logContents = executeCommand(getLinesCommand).trim();
        }

        return logContents;
    }

    public String executeCommands(String... commands) throws IOException {
        String finalCommand = String.join(Constants.CMD_DELIMITER, commands);
        return executeCommand(finalCommand);
    }

    private String executeCommand(String cmd) throws IOException {
        isTrue(!StringUtils.isEmpty(cmd), "cmd must not be null or empty");
        try (
            Session session = getSshClient().startSession();
            Session.Command command = session.exec(cmd)
        ) {
            String result = IOUtils.readFully(command.getInputStream()).toString();
            command.join();
            return result;
        }
    }

    private SSHClient getSshClient() throws IOException {
        if (sshClient == null || !sshClient.isConnected()) {
            sshClient = new SSHClient();
            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
            sshClient.connect(sshSetting.getHostname(), sshSetting.getPort());
            if (sshSetting.getKeyPath() != null) {
                sshClient.authPublickey(sshSetting.getUser(), getKeyProvider());
            } else {
                sshClient.authPassword(sshSetting.getUser(), sshSetting.getPassword());
            }
        }
        return sshClient;
    }

    private KeyProvider getKeyProvider() throws IOException {
        return sshSetting.getKeyPassphrase() != null ? sshClient.loadKeys(sshSetting.getKeyPath(), sshSetting.getKeyPassphrase()) : sshClient.loadKeys(sshSetting.getKeyPath());
    }

}
