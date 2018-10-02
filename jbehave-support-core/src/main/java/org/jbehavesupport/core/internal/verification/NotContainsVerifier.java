package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.internal.verification.VerifierNames.*;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notNull;

import org.springframework.stereotype.Component;

@Component
public final class NotContainsVerifier extends AbstractVerifier {
    @Override
    public String name() {
        return NOT_CONTAINS;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        notNull(expected, "Expected value must be provided");
        notNull(actual, "Can not execute NOT_CONTAINS verification with null value");

        if (actual == expected) {
            throwAssertionError("'%s' must not contain '%s'", actual, expected);
        }

        isInstanceOf(String.class, actual, "compared value must be of type String");
        isInstanceOf(String.class, expected, "expected value must be of type String");

        if (((String) actual).contains((String) expected)) {
            throwAssertionError("'%s' must not contain '%s'", actual, expected);
        }
    }
}
