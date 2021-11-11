package org.jbehavesupport.core.internal.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.jbehavesupport.core.verification.VerifierNames.MATH_EQ;

@Component
@RequiredArgsConstructor
public final class MathEqualsVerifier extends AbstractVerifier {

    public static final String MESSAGE_PARSE_OK = "value %s (originally written as '%s') is not mathematically equal to %s (originally written as '%s')";
    public static final String MESSAGE_PARSE_NOK = "couldn't parse either or both value/-s to a number: '%s', '%s'";
    final ConversionService conversionService;

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
            Double actualDouble = conversionService.convert(actual, Double.class);
            Double expectedDouble = conversionService.convert(expected, Double.class);
            if (Objects.equals(actualDouble, expectedDouble)) {
                return;
            }
            throwAssertionError(MESSAGE_PARSE_OK, actualDouble, actual, expectedDouble, expected);
        } catch (ConversionException e) {
            throwAssertionError(MESSAGE_PARSE_NOK, actual, expected);
        }
    }
}
