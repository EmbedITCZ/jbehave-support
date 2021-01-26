package org.jbehavesupport.core.internal.verification;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import static org.jbehavesupport.core.verification.VerifierNames.GE;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

@Component
@RequiredArgsConstructor
public final class GreaterThanOrEqualVerifier extends AbstractVerifier {

    final ConversionService conversionService;

    @Override
    public String name() {
        return GE;
    }

    @Override
    public void verify(Object actual, Object expected) {
        isInstanceOf(Comparable.class, actual, "compared value must be of type Comparable");
        isTrue(conversionService.canConvert(expected.getClass(), actual.getClass()), expected + " can not be converted to " + actual.getClass());

        Object expectedComparable = conversionService.convert(expected, actual.getClass());
        if (((Comparable) actual).compareTo(expectedComparable) < 0) {
            throwAssertionError("value '%s' is not greater than or equal to '%s'", actual, expected);
        }
    }

}
