package org.jbehavesupport.core.ssh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.convertTable;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.verification.VerifierNames;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public final class SshSteps {

    @Autowired
    private TestContext testContext;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Autowired
    private ExamplesEvaluationTableConverter tableConverter;

    @Autowired
    private VerifierResolver verifierResolver;

    @Value("${ssh.max.assert.count:10}")
    private int maxSoftAssertCount;

    private ZonedDateTime scenarioStart;

    @BeforeScenario
    public void init() {
        scenarioStart = ZonedDateTime.now();
    }

    private String readLog(String systemQualifier, ZonedDateTime startTime) {
        StringBuilder fetchedLog = new StringBuilder();
        List<SshTemplate> sshTemplates = resolveSshTemplates(systemQualifier);
        for (SshTemplate sshTemplate : sshTemplates) {
            try {
                SshLog sshLog = sshTemplate.copyLog(startTime, ZonedDateTime.now());
                fetchedLog.append(sshLog.getLogContents());
            } catch (IOException ex) {
                log.error("error fetching {}({}) log: {}", systemQualifier, sshTemplate.getSshSetting(), ex);
            }
        }

        return fetchedLog.toString();
    }

    @Given("log timestamp is saved as [$startTimeAlias]")
    public void markStartTime(String startTimeAlias) {
        testContext.put(startTimeAlias, ZonedDateTime.now());
    }

    @Then("the following data are present in [$systemQualifier] log:$presentData")
    public void logContainsData(String systemQualifier, String stringTable) {
        checkDataPresence(systemQualifier, scenarioStart, stringTable, verifierResolver.getVerifierByName(VerifierNames.CONTAINS));
    }

    @Then("the following data are present in [$systemQualifier] log since [$startTimeAlias]:$presentData")
    public void logContainsData(String systemQualifier, String startTimeAlias, String stringTable) {
        checkDataPresence(systemQualifier, testContext.get(startTimeAlias), stringTable, verifierResolver.getVerifierByName(VerifierNames.CONTAINS));
    }

    @Then("the following data are not present in [$systemQualifier] log:$missingData")
    public void dataNotInLog(String systemQualifier, String stringTable) {
        checkDataPresence(systemQualifier, scenarioStart, stringTable, verifierResolver.getVerifierByName(VerifierNames.NOT_CONTAINS));
    }

    private void checkDataPresence(String systemQualifier, ZonedDateTime startTime, String stringTable, Verifier verifier) {
        ExamplesTable searchData = (ExamplesTable) tableConverter.convertValue(stringTable, null);
        notNull(searchData, "searchData can't be null");
        isTrue((searchData.getHeaders().size() == 1) ||
                (searchData.getHeaders().size() == 2 && searchData.getHeaders().contains(VERIFIER)),
            "searchData must have only one search data column (or one search data column and a verifier)");
        String logData = readLog(systemQualifier, startTime);
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
                Verifier resolvedVerifier = resolveVerifier(row.get(VERIFIER), verifier);
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

    private Verifier resolveVerifier(String verifierName, Verifier defaultVerifier) {
        return (StringUtils.isNotEmpty(verifierName)) ? verifierResolver.getVerifierByName(verifierName) : defaultVerifier;
    }

    private String getSearchColumnName(ExamplesTable searchData) {
        return searchData.getHeaders()
            .stream()
            .filter(e -> !VERIFIER.equals(e))
            .findAny()
            .get();
    }

}
