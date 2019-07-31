package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.verification.VerifierNames.CONTAINS;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notNull;

import org.springframework.stereotype.Component;

@Component
public final class ContainsVerifier extends AbstractVerifier {
    @Override
    public String name() {
        return CONTAINS;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        notNull(expected, "Expected value must be provided");
        notNull(actual, "Can not execute CONTAINS verification with null value");

        isInstanceOf(String.class, expected, "expected value must be of type String");

        if (!actual.toString().contains((String) expected)) {
            throwAssertionError("'%s' must contain '%s'", actual, expected);
        }
    }
}
