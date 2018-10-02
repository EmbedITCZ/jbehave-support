package org.jbehavesupport.core.internal.verification;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.util.Collection;

import org.jbehavesupport.core.verification.Verifier;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractVerifier implements Verifier {

    protected final void throwAssertionError(String message, Object... args) throws AssertionError {
        throw new AssertionError(String.format(message, args));
    }

    final void basicSizeVerifierChecks(final Object actual, final Object expected) {
        notNull(actual, "Actual value must be provided.");
        notNull(expected, "Expected value must be provided.");
        isInstanceOf(Collection.class, actual);
        isTrue(StringUtils.isNumeric(expected.toString()), "Expected value must be numeric");
    }
}
