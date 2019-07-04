package org.jbehavesupport.core.verification;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.DATA;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.EXPECTED_VALUE;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;
import static org.jbehavesupport.core.internal.ExamplesTableUtil.convertTable;

@Component
@RequiredArgsConstructor
public class VerificationSteps {

    @Value("${verifier.max.assert.count:10}")
    private int maxSoftAssertCount;

    private final EqualsVerifier equalsVerifier;
    private final VerifierResolver verifierResolver;
    private final ExamplesEvaluationTableConverter tableConverter;

    @Then("following data are compared: $stringTable")
    public void compareRows(String stringTable) {
        List<Map<String, String>> convertedTable = convertTable((ExamplesTable) tableConverter.convertValue(stringTable, null));
        SoftAssertions softly = new SoftAssertions();
        convertedTable.forEach(e -> verifyRow(e, softly));
        softly.assertAll();
    }

    private void verifyRow(Map<String, String> tableRow, SoftAssertions softly) {
        if (softly.errorsCollected().size() >= maxSoftAssertCount) {
            softly.assertAll();
        }
        softly.assertThatCode(() -> resolveVerifier(tableRow.get(VERIFIER)).verify(tableRow.get(DATA), tableRow.get(EXPECTED_VALUE)))
            .doesNotThrowAnyException();
    }

    private Verifier resolveVerifier(String verifierName) {
        return (StringUtils.isNotEmpty(verifierName)) ? verifierResolver.getVerifierByName(verifierName) : equalsVerifier;
    }
}
