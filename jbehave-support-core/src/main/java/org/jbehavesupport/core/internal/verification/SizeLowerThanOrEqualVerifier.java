package org.jbehavesupport.core.internal.verification;

import org.springframework.stereotype.Component;

import java.util.Collection;

import static org.jbehavesupport.core.verification.VerifierNames.SIZE_LE;

@Component
public final class SizeLowerThanOrEqualVerifier extends AbstractVerifier {

    @Override
    public String name() {
        return SIZE_LE;
    }

    @Override
    public void verify(Object actual, Object expected) {
        basicSizeVerifierChecks(actual, expected);

        if (((Collection) actual).size() <= Integer.parseInt(expected.toString())) {
            return;
        }

        throwAssertionError("Collection size %s is not lower than or equal to expected: %s.", ((Collection) actual).size(), expected);
    }

}
