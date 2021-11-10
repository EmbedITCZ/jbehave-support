package org.jbehavesupport.core.internal.verification;

import org.springframework.stereotype.Component;

import static org.jbehavesupport.core.verification.VerifierNames.MATH_EQ;

@Component
public final class MathEqualsVerifier extends AbstractVerifier {

    public static final String MESSAGE_PARSE_OK = "value %s (originally written as '%s') is not mathematically equal to %s (originally written as '%s')";
    public static final String MESSAGE_PARSE_NOK = "couldn't parse either or both value/-s to a number: '%s', '%s'";

    @Override
    public String name() {
        return MATH_EQ;
    }

    @Override
    public void verify(Object actual, Object expected) {
        if (actual == expected) {
            return;
        }

        try {
            Double actualDouble = Double.valueOf(actual.toString());
            Double expectedDouble = Double.valueOf(expected.toString());
            if (actualDouble.equals(expectedDouble)) {
                return;
            }
            throwAssertionError(MESSAGE_PARSE_OK, actualDouble, actual, expectedDouble, expected);
        } catch (NumberFormatException e) {
            throwAssertionError(MESSAGE_PARSE_NOK, actual, expected);
        }
    }
}
