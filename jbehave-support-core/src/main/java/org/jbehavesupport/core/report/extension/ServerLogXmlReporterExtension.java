package org.jbehavesupport.core.report.extension;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.ScenarioType;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.internal.FileNameResolver;
import org.jbehavesupport.core.report.ReportRenderingPhase;
import org.jbehavesupport.core.ssh.SshHandler;
import org.jbehavesupport.core.ssh.SshReportType;
import org.jbehavesupport.core.ssh.SshTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

@RequiredArgsConstructor
public class ServerLogXmlReporterExtension extends AbstractXmlReporterExtension {

    private final TestContext testContext;
    private final FileNameResolver fileNameResolver;
    private final SshHandler sshHandler;
    private final ConfigurableListableBeanFactory beanFactory;

    private static final String SSH_XML_REPORTER_EXTENSION = "serverLog";
    private static final String LOG = "log";
    private static final String FILE = "file";
    private static final String TEXT = "text";
    private static final String SYSTEM = "system";
    private static final String FAIL = "fail";
    private static final String FILE_NAME_PATTERN = "LOG_%s_%s.txt";

    private MultiKeyMap<String, String> logContents = MultiKeyMap.multiKeyMap(new LRUMap());

    @Value("${ssh.reporting.maxLogLength:10000}")
    private Long maxLength;

    @Setter
    private SshReportType sshReportMode;

    @Value("${ssh.reporting.logOnFailure:false}")

    private boolean loggingOnFailure;

    @Value("${web.reporting.directory:./target/reports}")
    private String logFileDirectory;

    @Value("${ssh.reporting.mode:CACHE}")
    SshReportType defaultReportType;

    @BeforeScenario
    public void init() {
        sshReportMode = defaultReportType;
    }

    @AfterScenario
    public void after() {
        if (sshReportMode == SshReportType.CACHE) {
            sshHandler.getLogCache().entrySet().forEach(entry -> {
                MultiKey newKey = new MultiKey(entry.getKey().getKey(0), entry.getKey().getKey(1), entry.getKey().getKey(2), "N/A", "N/A");
                registerLogContent(newKey, (entry.getValue()));
            });
        } else if (sshReportMode == SshReportType.FULL) {
            registerLogContent(sshHandler.getTemplateLogs(getSshTemplates()));
        } else if (sshReportMode == SshReportType.TEMPLATE) {
            registerLogContent(sshHandler.getTemplateLogs(getReportableSshTemplates(getSshTemplates())));
        }
    }

    @AfterScenario(uponType = ScenarioType.ANY, uponOutcome = AfterScenario.Outcome.FAILURE)
    public void afterFailedScenario() {
        if (sshReportMode != SshReportType.TEMPLATE && loggingOnFailure) {
            registerLogContent(sshHandler.getTemplateLogs(getReportableSshTemplates(getSshTemplates())));
        }
    }

    @Override
    public ReportRenderingPhase getReportRenderingPhase() {
        return ReportRenderingPhase.AFTER_STORY;
    }

    public void registerLogContent(MultiKeyMap<String, String> logContents) {
        logContents.entrySet().forEach(entry -> registerLogContent(entry.getKey(), entry.getValue()));
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

    private void printContent(Writer writer) {
        logContents.entrySet().forEach(
            entry -> {
                MultiKey key = entry.getKey();
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

    private void printLogFile(Writer writer, String logContent, String qualifier) {
        File logFile = new File(LocalTime.now().toNanoOfDay() + ".txt");
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

    private <T> Map<String, List<T>> getSshTemplatesForType(Class<T> clazz) {
        Map<String, List<T>> sshTemplates = new HashMap<>();
        String[] beanNames = beanFactory.getBeanNamesForType(clazz);
        for (String beanName : beanNames) {
            BeanDefinition bd = beanFactory.getMergedBeanDefinition(beanName);
            if (bd instanceof RootBeanDefinition) {
                Qualifier qualifier = ((RootBeanDefinition) bd).getResolvedFactoryMethod().getAnnotation(Qualifier.class);
                if (sshTemplates.get(qualifier.value()) == null) {
                    sshTemplates.put(qualifier.value(), new ArrayList<>());
                }
                sshTemplates.get(qualifier.value()).add((T) beanFactory.getBean(beanName));
            }
        }
        return sshTemplates;
    }

    private Map<String, List<SshTemplate>> getSshTemplates() {
        Map<String, List<SshTemplate[]>> sshTemplatesArray = getSshTemplatesForType(SshTemplate[].class);
        Map<String, List<SshTemplate>> sshTemplates = getSshTemplatesForType(SshTemplate.class);

        //merge both maps together to simple Map<String, List<SshTemplate>>
        sshTemplatesArray.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().flatMap(Arrays::stream).collect(Collectors.toList())))
            .forEach((k, v) -> sshTemplates.merge(k, v, (v1, v2) -> {
                v2.addAll(v1);
                return v2;
            }));

        return sshTemplates;
    }

    private Map<String, List<SshTemplate>> getReportableSshTemplates(Map<String, List<SshTemplate>> sshTemplates) {
        return sshTemplates.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream()
                    .filter(SshTemplate::isReportable)
                    .collect(Collectors.toList())));
    }
}
