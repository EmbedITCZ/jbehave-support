package org.jbehavesupport.core.internal.verification;

import org.springframework.stereotype.Component;

import java.util.Collection;

import static org.jbehavesupport.core.verification.VerifierNames.SIZE_GE;

@Component
public final class SizeGreaterThanOrEqualVerifier extends AbstractVerifier {

    @Override
    public String name() {
        return SIZE_GE;
    }

    @Override
    public void verify(Object actual, Object expected) {
        basicSizeVerifierChecks(actual, expected);

        if (((Collection) actual).size() >= Integer.parseInt(expected.toString())) {
            return;
        }

        throwAssertionError("Collection size %s is not greater than or equal to expected: %s.", ((Collection) actual).size(), expected);
    }

}
