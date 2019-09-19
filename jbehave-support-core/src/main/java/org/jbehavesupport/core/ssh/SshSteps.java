package org.jbehavesupport.core.ssh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.verification.ContainsVerifier;
import org.jbehavesupport.core.internal.verification.NotContainsVerifier;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.convertTable;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Slf4j
@Component
@RequiredArgsConstructor
public final class SshSteps {

    private final TestContext testContext;

    private final ConfigurableListableBeanFactory beanFactory;

    private final ExamplesEvaluationTableConverter tableConverter;

    private final VerifierResolver verifierResolver;

    private final ContainsVerifier containsVerifier;

    private final NotContainsVerifier notContainsVerifier;

    @Value("${ssh.max.assert.count:10}")
    private int maxSoftAssertCount;

    private ZonedDateTime scenarioStart;
    private ZonedDateTime logStartTime;
    private ZonedDateTime logEndTime;

    private MultiKeyMap<MultiKey, String> logCache = MultiKeyMap.multiKeyMap(new LRUMap());

    @BeforeScenario
    public void init() {
        scenarioStart = ZonedDateTime.now();
        logStartTime = null;
        logEndTime = null;
        logCache.clear();
    }

    public String readLog(String systemQualifier, ZonedDateTime startTime, ZonedDateTime endTime) {
        if (logCache.containsKey(startTime, endTime, systemQualifier)) {
            log.info("Log found in cache.");
            return logCache.get(startTime, endTime, systemQualifier);
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
        logCache.put(new MultiKey(startTime, endTime, systemQualifier), fetchedLog.toString());
        return fetchedLog.toString();
    }

    @Given("current time is saved as log timestamp [$logTimeAlias]")
    public void markLogTime(String logTimeAlias) {
        testContext.put(logTimeAlias, ZonedDateTime.now());
    }

    @Given("log start timestamp is set to current time")
    public void saveLogStartTime(){
        logStartTime = ZonedDateTime.now();
    }

    @Given("log end timestamp is set to current time")
    public void saveLogEndTime(){
        logEndTime = ZonedDateTime.now().plusSeconds(1);
    }

    @Given("log start timestamp is set to [$contextAlias]")
    public void saveLogStartTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        logStartTime = ZonedDateTime.parse(contextAlias.getValue());
    }

    @Given("log end timestamp is set to [$contextAlias]")
    public void setLogEndTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        logEndTime = ZonedDateTime.parse(contextAlias.getValue()).plusSeconds(1);
    }

    @Then("the following data are present in [$systemQualifier] log:$presentData")
    public void logContainsData(String systemQualifier, String stringTable) {
        checkDataPresence(systemQualifier, scenarioStart, stringTable, containsVerifier);
    }

    /** use logContainsData(String systemQualifier, String stringTable) instead
     * If you set timestamps via separate steps, log reading is more accurate and use cache
     */
    @Deprecated
    @Then("the following data are present in [$systemQualifier] log since [$startTimeAlias]:$presentData")
    public void logContainsData(String systemQualifier, String startTimeAlias, String stringTable) {
        checkDataPresence(systemQualifier, testContext.get(startTimeAlias), stringTable, containsVerifier);
    }

    @Then("the following data are not present in [$systemQualifier] log:$missingData")
    public void dataNotInLog(String systemQualifier, String stringTable) {
        checkDataPresence(systemQualifier, scenarioStart, stringTable, notContainsVerifier);
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

    private String getSearchColumnName(ExamplesTable searchData) {
        return searchData.getHeaders()
            .stream()
            .filter(e -> !VERIFIER.equals(e))
            .findAny()
            .get();
    }

}
