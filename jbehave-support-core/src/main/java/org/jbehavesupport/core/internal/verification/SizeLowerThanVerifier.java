package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.verification.VerifierNames.SIZE_LT;

import java.util.Collection;

import org.springframework.stereotype.Component;

@Component
public final class SizeLowerThanVerifier extends AbstractVerifier {

    @Override
    public String name() {
        return SIZE_LT;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        basicSizeVerifierChecks(actual, expected);

        if (((Collection) actual).size() < Integer.parseInt(expected.toString())) {
            return;
        }

        throwAssertionError("Collection size %s is not lower than expected: %s.", ((Collection) actual).size(), expected);
    }
}
