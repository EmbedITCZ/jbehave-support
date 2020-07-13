package org.jbehavesupport.core.splunk;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.verification.VerifierResolver;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.DATA;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.convertTable;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Slf4j
@Component
@RequiredArgsConstructor
public final class SplunkSteps {
    private static final String SPLUNK_RESULT_KEY = "splunk_search_result";

    private final TestContext testContext;
    private final ExamplesEvaluationTableConverter examplesTableFactory;
    private final ConfigurableListableBeanFactory beanFactory;

    @NonNull
    private VerifierResolver verifierResolver;

    @Given("the Splunk search query is performed:$splunkQuery")
    @When("the Splunk search query is performed:$splunkQuery")
    public void executeSplunkSearchQuery(ExpressionEvaluatingParameter<String> splunkQueryExpression) {
        testContext.put(SPLUNK_RESULT_KEY, resolveSplunkClient().search(splunkQueryExpression.getValue()));
    }

    @Given("the Splunk search query is performed within [$earliestTime] and [$latestTime]:$splunkQuery")
    @When("the Splunk search query is performed within [$earliestTime] and [$latestTime]:$splunkQuery")
    public void executeSplunkSearchQuery(ExpressionEvaluatingParameter<String> earliestTimeExpression, ExpressionEvaluatingParameter<String> latestTimeExpression, ExpressionEvaluatingParameter<String> splunkQueryExpression) {
        testContext.put(SPLUNK_RESULT_KEY, resolveSplunkClient().search(splunkQueryExpression.getValue(), earliestTimeExpression.getValue(), latestTimeExpression.getValue()));
    }

    @Then("the Splunk search result set has $rowCount row(s)")
    public void compareSplunkSearchResultSetRowCount(ExpressionEvaluatingParameter<Integer> rowCount) {
        assertThat(getSplunkSearchResult())
            .as("returned Splunk search result set row count does not match expected count")
            .hasSize(rowCount.getValue());
    }

    @Then("the Splunk search result match these rules:$rulesData")
    public void matchSplunkSearchResultAgainstRules(String verifiersData) {
        compareExampleTableWithListOfSplunkSearchResultEntries(examplesTableFactory.convertValue(verifiersData, null));
    }

    private void compareExampleTableWithListOfSplunkSearchResultEntries(ExamplesTable verifiersData) {
        List<SplunkSearchResultEntry> splunkSearchData = getSplunkSearchResult();
        notNull(verifiersData, "verifier meta data can't be null");
        notNull(splunkSearchData, "search result set data can't be null");
        isTrue(verifiersData.getHeaders().size() == 2 && verifiersData.getHeaders().contains(VERIFIER), "searchData must have one search data column and a verifier)");

        assertThat(splunkSearchData)
            .as("no Splunk search query match")
            .isNotEmpty();
        verifySearchResultEntries(verifiersData, splunkSearchData);
    }

    private void verifySearchResultEntries(ExamplesTable verifiersData, List<SplunkSearchResultEntry> splunkSearchData) {
        convertTable(verifiersData).forEach(row ->
            assertThat(splunkSearchData)
                .as("all found Splunk search result data meet all verifier definitions")
                .allSatisfy(
                    splunkSearchResultEntry -> {
                        String verifierName = row.get(VERIFIER);
                        String verifierData = row.get(DATA);
                        verifierResolver.getVerifierByName(verifierName).verify(splunkSearchResultEntry.getMessage(), verifierData);
                    }
                )
        );
    }

    private List<SplunkSearchResultEntry> getSplunkSearchResult() {
        return testContext.get(SPLUNK_RESULT_KEY);
    }

    private SplunkClient resolveSplunkClient() {
        try {
            return beanFactory.getBean(SplunkClient.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("SplunkSteps require single SplunkClient bean", e);
        }
    }
}
