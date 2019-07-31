package org.jbehavesupport.core.internal.verification;

import static org.jbehavesupport.core.verification.VerifierNames.GT;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class GreaterThanVerifier extends AbstractVerifier {

    final ConversionService conversionService;

    @Override
    public String name() {
        return GT;
    }

    @Override
    public void verify(final Object actual, final Object expected) {
        isInstanceOf(Comparable.class, actual, "compared value must be of type Comparable");
        isTrue(conversionService.canConvert(expected.getClass(), actual.getClass()), expected + " can not be converted to " + actual.getClass());

        Object expectedComparable = conversionService.convert(expected, actual.getClass());
        if (((Comparable) actual).compareTo(expectedComparable) <= 0) {
            throwAssertionError("value '%s' is not greater than '%s'", actual, expected);
        }
    }
}
