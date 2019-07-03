package org.jbehavesupport.core.verification;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.SoftAssertions;
import org.codehaus.plexus.util.StringUtils;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.verification.VerifierNames;
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

    private final VerifierResolver verifierResolver;
    private final ExamplesEvaluationTableConverter tableConverter;

    @Then("following data are compared: $stringTable")
    public void compareRows(String stringTable) {
        List<Map<String, String>> convertedTable = convertTable((ExamplesTable) tableConverter.convertValue(stringTable, null));
        SoftAssertions softly = new SoftAssertions();
        convertedTable.forEach(e -> verifyRow(e, verifierResolver.getVerifierByName(VerifierNames.EQ), DATA, EXPECTED_VALUE, softly));
        softly.assertAll();
    }

    private void verifyRow(Map<String, String> tableRow, Verifier verifier, String searchColumn1, String searchColumn2, SoftAssertions softly) {
        if (softly.errorsCollected().size() >= maxSoftAssertCount) {
            softly.assertAll();
        }
        softly.assertThatCode(() -> resolveVerifier(tableRow.get(VERIFIER), verifier).verify(tableRow.get(searchColumn1), tableRow.get(searchColumn2)))
            .doesNotThrowAnyException();
    }

    private Verifier resolveVerifier(String verifierName, Verifier defaultVerifier) {
        return (StringUtils.isNotEmpty(verifierName)) ? verifierResolver.getVerifierByName(verifierName) : defaultVerifier;
    }
}
