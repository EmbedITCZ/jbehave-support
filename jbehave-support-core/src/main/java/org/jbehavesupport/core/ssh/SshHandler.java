package org.jbehavesupport.core.ssh;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.convertTable;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * This class implements steps for testing ssh and provides customization option.
 * This handler is registered as default. Register our own instance for customization.
 * <p>
 * Example:
 * <pre>
 * &#064;Configuration
 * public class MyTestConfiguration {
 *
 *     &#064;Bean
 *     public CustomSshHandler customSshHandler() {
 *         return new CustomSshHandler();
 *     }
 *
 * }
 *
 * public class CustomSshHandler extends SshHandler {
 *
 *     //project specific customization
 *
 * }
 * </pre>
 */

@Slf4j
@RequiredArgsConstructor
public class SshHandler {
    private final TestContext testContext;
    private final ConfigurableListableBeanFactory beanFactory;
    private final ExamplesEvaluationTableConverter tableConverter;
    private final VerifierResolver verifierResolver;

    @Value("${ssh.max.assert.count:10}")
    private int maxSoftAssertCount;

    private ZonedDateTime scenarioStart;
    private ZonedDateTime logStartTime;
    private ZonedDateTime logEndTime;

    @Getter
    private MultiKeyMap<MultiKey, String> logCache = MultiKeyMap.multiKeyMap(new LRUMap());

    @BeforeScenario
    public void init() {
        scenarioStart = ZonedDateTime.now();
        logStartTime = null;
        logEndTime = null;
        logCache.clear();
    }

    public void markLogTime(String logTimeAlias) {
        testContext.put(logTimeAlias, ZonedDateTime.now());
    }

    public void saveLogStartTime() {
        logStartTime = ZonedDateTime.now();
    }

    public void saveLogEndTime() {
        logEndTime = ZonedDateTime.now().plusSeconds(1);
    }

    public void setLogStartTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        logStartTime = ZonedDateTime.parse(contextAlias.getValue());
    }

    public void setLogEndTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        logEndTime = ZonedDateTime.parse(contextAlias.getValue()).plusSeconds(1);
    }

    public void checkLogDataPresence(String systemQualifier, String stringTable, Verifier verifier) {
        checkDataPresence(systemQualifier, scenarioStart, stringTable, verifier);
    }

    /**
     * @deprecated use logContainsData(String systemQualifier, String stringTable) instead
     * If you set timestamps via separate steps, log reading is more accurate and use cache
     */
    @Deprecated
    public void checkLogDataPresence(String systemQualifier, String startTimeAlias, String stringTable, Verifier verifier) {
        checkDataPresence(systemQualifier, testContext.get(startTimeAlias), stringTable, verifier);
    }

    /**
     * By overriding this method you can change how log is read.
     *
     * @param systemQualifier SshTemplate qualifier
     * @param startTime       log start search timestamp
     * @param endTime         log search end timestamp
     */
    protected String readLog(String systemQualifier, ZonedDateTime startTime, ZonedDateTime endTime) {
        if (logCache.containsKey(systemQualifier, startTime, endTime)) {
            log.info("Log found in cache.");
            return logCache.get(systemQualifier, startTime, endTime);
        }
        StringBuilder fetchedLog = new StringBuilder();
        List<SshTemplate> sshTemplates = resolveSshTemplates(systemQualifier);
        for (SshTemplate sshTemplate : sshTemplates) {
            try {
                SshLog sshLog = sshTemplate.copyLog(startTime, endTime);
                fetchedLog.append(sshLog.getLogContents());
            } catch (IOException ex) {
                log.error("error fetching {}({}) log: {}", systemQualifier, sshTemplate.getSshSetting(), ex);
            }
        }
        logCache.put(new MultiKey(systemQualifier, startTime, endTime), fetchedLog.toString());
        return fetchedLog.toString();
    }

    private void checkDataPresence(String systemQualifier, ZonedDateTime startTime, String stringTable, Verifier verifier) {
        ExamplesTable searchData = (ExamplesTable) tableConverter.convertValue(stringTable, null);
        notNull(searchData, "searchData can't be null");
        isTrue((searchData.getHeaders().size() == 1) ||
                (searchData.getHeaders().size() == 2 && searchData.getHeaders().contains(VERIFIER)),
            "searchData must have only one search data column (or one search data column and a verifier)");
        String logData = readLog(systemQualifier,
            logStartTime != null ? logStartTime : startTime,
            logEndTime != null ? logEndTime : ZonedDateTime.now().plusSeconds(1));
        assertThat(logData)
            .as("log not found in " + systemQualifier + " log")
            .isNotEmpty();
        verifyRows(verifier, searchData, logData);
    }

    /**
     * By overriding this method you can change how rows are verified.
     *
     * @param verifier   verifier to be used
     * @param searchData exampleTable to be verified
     * @param logData    log content
     */
    protected void verifyRows(Verifier verifier, ExamplesTable searchData, String logData) {
        List<Map<String, String>> convertedTable = convertTable(searchData);
        String searchColumn = getSearchColumnName(searchData);

        SoftAssertions softly = new SoftAssertions();
        for (Map<String, String> row : convertedTable) {
            if (softly.errorsCollected().size() >= maxSoftAssertCount) {
                log.error("Maximum number ({}) of assertions failed, it is possible that more errors may have occurred than those displayed", maxSoftAssertCount);
                softly.assertAll();
            }

            softly.assertThatCode(() -> {
                Verifier resolvedVerifier = verifierResolver.getVerifierByName(row.get(VERIFIER), verifier);
                resolvedVerifier.verify(logData, row.get(searchColumn));
            }).doesNotThrowAnyException();
        }
        softly.assertAll();
    }

    @SuppressWarnings("squid:S1166")
    private List<SshTemplate> resolveSshTemplates(String sshId) {
        List<SshTemplate> sshTemplates = new ArrayList<>();
        try {
            SshTemplate[] sshTemplatesArray = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, SshTemplate[].class, sshId);
            sshTemplates.addAll(Arrays.asList(sshTemplatesArray));
        } catch (NoSuchBeanDefinitionException ex) {
            try {
                SshTemplate sshTemplate = BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, SshTemplate.class, sshId);
                sshTemplates.add(sshTemplate);
            } catch (NoSuchBeanDefinitionException e) {
                throw new IllegalArgumentException("SshSteps requires SshTemplate (or SshTemplate[]) bean with qualifier [" + sshId + "] but no was found", e);
            }
        }

        return sshTemplates;
    }

    /**
     * By overriding this method you can modify default search column name to be fixed etc.
     *
     * @param searchData example table to be searched
     * @return String search column name
     */
    protected String getSearchColumnName(ExamplesTable searchData) {
        Optional<String> searchColumnName = searchData.getHeaders()
            .stream()
            .filter(e -> !VERIFIER.equals(e))
            .findAny();
        if (searchColumnName.isPresent()) {
            return searchColumnName.get();
        } else {
            throw new IllegalArgumentException("No search column found in example table");
        }
    }

    public MultiKeyMap<String, String> getTemplateLogs(Map<String, List<SshTemplate>> sshTemplates){
        ZonedDateTime scenarioEnd = ZonedDateTime.now();
        MultiKeyMap<String, String> templateLogs = MultiKeyMap.multiKeyMap(new LRUMap());
        sshTemplates.entrySet().forEach(entry ->
            entry.getValue().forEach(sshTemplate -> {
                MultiKey<String> multiKey = new MultiKey(entry.getKey(),
                    scenarioStart,
                    scenarioEnd,
                    sshTemplate.getSshSetting().getHostname() + ":" + sshTemplate.getSshSetting().getPort(),
                    sshTemplate.getSshSetting().getLogPath());
                try {
                    String sshLog = sshTemplate.copyLog(scenarioStart, scenarioEnd).getLogContents();
                    templateLogs.put(multiKey, sshLog);
                } catch (Exception e) {
                    templateLogs.put(multiKey, ExceptionUtils.getStackTrace(e));
                }
            })
        );
        return templateLogs;
    }
}
