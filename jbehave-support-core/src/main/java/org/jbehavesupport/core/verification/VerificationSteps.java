package org.jbehavesupport.core.verification;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.jbehavesupport.core.internal.ExampleTableConstraints.DATA;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.EXPECTED_VALUE;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;

@Component
@RequiredArgsConstructor
public class VerificationSteps {

    @Value("${verifier.max.assert.count:10}")
    private int maxSoftAssertCount;

    private final VerifierResolver verifierResolver;

    private final EqualsVerifier equalsVerifier;

    @Then("following data are compared: $examplesTable")
    public void compareRows(ExamplesTable examplesTable) {
        SoftAssertions softly = new SoftAssertions();
        examplesTable.getRows().forEach(e -> verifyRow(e, softly));
        softly.assertAll();
    }

    private void verifyRow(Map<String, String> tableRow, SoftAssertions softly) {
        if (softly.errorsCollected().size() >= maxSoftAssertCount) {
            softly.assertAll();
        }
        softly.assertThatCode(() -> verify(tableRow))
            .doesNotThrowAnyException();
    }

    private void verify(Map<String, String> tableRow) {
        verifierResolver.getVerifierByName(tableRow.get(VERIFIER), equalsVerifier)
            .verify(tableRow.get(DATA), tableRow.get(EXPECTED_VALUE));
    }
}
