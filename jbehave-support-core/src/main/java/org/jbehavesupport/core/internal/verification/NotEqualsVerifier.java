package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.internal.util.ArrayComparator.arraysEquals;
import static org.jbehavesupport.core.internal.verification.VerifierNames.*;
import static org.springframework.util.Assert.notNull;

import org.springframework.stereotype.Component;

@Component
public final class NotEqualsVerifier extends AbstractVerifier {

    private static final String MESSAGE = "'%s' must be different from '%s'";

    @Override
    public String name() {
        return NE;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        if (actual == expected) {
            throwAssertionError(MESSAGE, actual, expected);
        }
        notNull(actual, "Actual value must be provided");
        if (actual.equals(expected)) {
            throwAssertionError(MESSAGE, actual, expected);
        }

        if (actual.getClass().isArray() && expected != null && expected.getClass().isArray() && arraysEquals(actual, expected)) {
            throwAssertionError(MESSAGE, actual, expected);
        }

        if (actual.toString().equals(String.valueOf(expected))) {
            throwAssertionError(MESSAGE, actual, expected);
        }
    }

}
