package org.jbehavesupport.core.verification;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.internal.verification.EqualsVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.DATA;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.EXPECTED_VALUE;
import static org.jbehavesupport.core.internal.ExampleTableConstraints.VERIFIER;

@Component
@RequiredArgsConstructor
public class VerificationSteps {

    @Value("${verifier.max.assert.count:10}")
    private int maxSoftAssertCount;

    private final EqualsVerifier equalsVerifier;
    private final VerifierResolver verifierResolver;

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
        softly.assertThatCode(() -> resolveVerifier(tableRow.get(VERIFIER)).verify(tableRow.get(DATA), tableRow.get(EXPECTED_VALUE)))
            .doesNotThrowAnyException();
    }

    private Verifier resolveVerifier(String verifierName) {
        return (StringUtils.isNotEmpty(verifierName)) ? verifierResolver.getVerifierByName(verifierName) : equalsVerifier;
    }
}
