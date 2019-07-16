package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.ssh.SshLog;
import org.jbehavesupport.core.ssh.SshTemplate;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

@RequiredArgsConstructor
public class ServerLogXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String SSH_XML_REPORTER_EXTENSION = "serverLog";
    private static final String LOG = "log";
    private static final String SYSTEM = "system";
    private static final String FAIL = "fail";

    private final ConfigurableListableBeanFactory beanFactory;

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
        Map<String, List<SshTemplate>> sshTemplates = getSshTemplates();
        for (String qualifier : sshTemplates.keySet()) {
            printBegin(writer, SYSTEM, getSshQualifierAttributes(qualifier));
            for (SshTemplate sshTemplate : sshTemplates.get(qualifier)) {
                printBegin(writer, LOG, getSshTemplateAttributes(reportContext, sshTemplate));
                try {
                    SshLog sshLog = sshTemplate.copyLog(reportContext.startExecutionZoned(), reportContext.endExecutionZoned());
                    printCData(writer, sshLog.getLogContents());
                } catch (Exception e) {
                    printBegin(writer, FAIL);
                    printCData(writer, ExceptionUtils.getStackTrace(e));
                    printEnd(writer, FAIL);
                }
                printEnd(writer, LOG);
            }
            printEnd(writer, SYSTEM);
        }
    }

    private Map<String, String> getSshQualifierAttributes(String qualifier) {
        Map<String, String> sshQualifierAttributes = new HashMap<>();
        sshQualifierAttributes.put("id", qualifier);
        return sshQualifierAttributes;
    }

    private Map<String, String> getSshTemplateAttributes(ReportContext reportContext, SshTemplate sshTemplate) {
        Map<String, String> sshTemplateAttributes = new HashMap<>();
        sshTemplateAttributes.put("startDate", reportContext.startExecutionZoned().toString());
        sshTemplateAttributes.put("endDate", reportContext.endExecutionZoned().toString());
        sshTemplateAttributes.put("host", sshTemplate.getSshSetting().getHostname() + ":" + sshTemplate.getSshSetting().getPort());
        sshTemplateAttributes.put("logPath", sshTemplate.getSshSetting().getLogPath());
        return sshTemplateAttributes;
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
        sshTemplatesArray.entrySet()
            .stream()
            .forEach(entry -> {
                List<SshTemplate> flattenedSshTemplatesArray = entry.getValue()
                    .stream()
                    .flatMap(Arrays::stream)
                    .collect(Collectors.toList());
                if (sshTemplates.get(entry.getKey()) != null) {
                    sshTemplates.get(entry.getKey()).addAll(flattenedSshTemplatesArray);
                } else {
                    sshTemplates.put(entry.getKey(), flattenedSshTemplatesArray);
                }
            });

        return sshTemplates;
    }
}
