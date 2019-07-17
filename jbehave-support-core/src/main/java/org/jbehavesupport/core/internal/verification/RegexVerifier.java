package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.verification.VerifierNames.REGEX_MATCH;
import static org.springframework.util.Assert.notNull;

import org.springframework.stereotype.Component;

@Component
public final class RegexVerifier extends AbstractVerifier {
    @Override
    public String name() {
        return REGEX_MATCH;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        notNull(actual, "Actual value must be provided");

        if (!actual.toString().matches(expected.toString())) {
            throwAssertionError("'%s' does not match on regex: %s", actual, expected);
        }
    }
}
