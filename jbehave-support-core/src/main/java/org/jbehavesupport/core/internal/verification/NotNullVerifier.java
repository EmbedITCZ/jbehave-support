package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.verification.VerifierNames.NOT_NULL;

import org.springframework.stereotype.Component;

@Component
public class NotNullVerifier extends AbstractVerifier {
    @Override
    public String name() {
        return NOT_NULL;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        if (actual == null) {
            throwAssertionError("Not null value was expected, but null received");
        }
    }
}
