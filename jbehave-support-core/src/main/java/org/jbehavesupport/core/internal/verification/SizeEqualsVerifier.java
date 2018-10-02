package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.internal.verification.VerifierNames.SIZE_EQ;

import java.util.Collection;

import org.springframework.stereotype.Component;

@Component
public final class SizeEqualsVerifier extends AbstractVerifier {

    @Override
    public String name() {
        return SIZE_EQ;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        basicSizeVerifierChecks(actual, expected);

        if (((Collection) actual).size() == Integer.parseInt(expected.toString())) {
            return;
        }

        throwAssertionError("Collection has %s elements, but %s was expected.", ((Collection) actual).size(), expected);
    }
}
