package org.jbehavesupport.core.report.extension;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.io.FileUtils;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.jbehavesupport.core.report.ReportRenderingPhase;
import org.jbehavesupport.core.ssh.SshReportType;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class ServerLogXmlReporterExtension extends AbstractXmlReporterExtension {

    private final TestContext testContext;
    private final FileNameResolver fileNameResolver;

    private static final String SSH_XML_REPORTER_EXTENSION = "serverLog";
    private static final String LOG = "log";
    private static final String FILE = "file";
    private static final String TEXT = "text";
    private static final String SYSTEM = "system";
    private static final String FAIL = "fail";
    private static final String FILE_NAME_PATTERN = "LOG_%s_%s.txt";

    private MultiKeyMap<MultiKey, String> logContents = MultiKeyMap.multiKeyMap(new LRUMap());

    @Value("${ssh.reporting.maxLogLength:10000}")
    private Long maxLength;

    @Getter
    @Setter
    private SshReportType sshReportType;

    @Value("${ssh.reporting.logOnFailure:false}")
    @Getter
    private boolean loggingOnFailure;

    @Value("${web.reporting.directory:./target/reports}")
    private String logFileDirectory;

    @Override
    public ReportRenderingPhase getReportRenderingPhase() {
        return ReportRenderingPhase.AFTER_STORY;
    }

    public void registerLogContent(MultiKey multiKey, String logContent) {
        if (logContents.containsKey(multiKey)) {
            logContent += logContents.get(multiKey);
        }
        logContents.put(multiKey, logContent);
    }

    @Override
    public String getName() {
        return SSH_XML_REPORTER_EXTENSION;
    }

    @Override
    public Long getPriority() {
        // move log to end of chain
        return 100L;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        printContent(writer);
        logContents.clear();
    }

    private void printContent(final Writer writer) {
        logContents.entrySet().stream().forEach(
            entry -> {
                final MultiKey key = entry.getKey();
                printBegin(writer, SYSTEM, getSshQualifierAttributes(key.getKey(0).toString()));
                printBegin(writer, LOG, getSshAttributesFromKey(key));
                printLogContent(writer, key.getKey(0).toString(), entry.getValue());
                printEnd(writer, LOG);
                printEnd(writer, SYSTEM);
            }
        );
    }

    private void printLogContent(Writer writer, String qualifier, String sshLog) {
        if (sshLog.length() > maxLength) {
            printLogFile(writer, sshLog, qualifier);
        } else {
            printBegin(writer, TEXT);
            printCData(writer, sshLog);
            printEnd(writer, TEXT);
        }
    }

    private void printLogFile(final Writer writer, String logContent, String qualifier) {
        File logFile = new File(new Date().getTime() + ".txt");
        try {
            prepareDirectory();
            FileUtils.writeStringToFile(logFile, logContent, (String) null);
            File destinationFile = fileNameResolver.resolveFilePath(FILE_NAME_PATTERN, logFileDirectory, qualifier).toFile();
            FileUtils.copyFile(logFile, destinationFile);
            printBegin(writer, FILE);
            printString(writer, destinationFile.getName());
            printEnd(writer, FILE);
        } catch (IOException e) {
            printBegin(writer, FAIL);
            printCData(writer, ExceptionUtils.getStackTrace(e));
            printEnd(writer, FAIL);
        }
        logFile.delete();
    }

    private void prepareDirectory() throws IOException {
        if (!Paths.get(logFileDirectory).toFile().exists()) {
            Files.createDirectory(Paths.get(logFileDirectory));
            testContext.put("logFileDirectory", logFileDirectory);
        }
    }

    private Map<String, String> getSshQualifierAttributes(String qualifier) {
        Map<String, String> sshQualifierAttributes = new HashMap<>();
        sshQualifierAttributes.put(SYSTEM, qualifier);
        return sshQualifierAttributes;
    }

    private Map<String, String> getSshAttributesFromKey(MultiKey key) {
        Map<String, String> sshContextAttributes = new HashMap<>();
        sshContextAttributes.put("startDate", key.getKey(1).toString());
        sshContextAttributes.put("endDate", key.getKey(2).toString());
        sshContextAttributes.put("host", key.getKey(3).toString());
        sshContextAttributes.put("logPath", key.getKey(4).toString());
        return sshContextAttributes;
    }
}
