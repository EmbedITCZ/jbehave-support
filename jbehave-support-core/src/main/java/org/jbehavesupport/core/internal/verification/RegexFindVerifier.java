package org.jbehavesupport.core.internal.verification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import static org.jbehavesupport.core.internal.verification.VerifierNames.REGEX_FIND;

@Component
public class RegexFindVerifier extends AbstractVerifier {
    public String name() {
        return REGEX_FIND;
    }

    public void verify(Object actual, Object expected) {
        Assert.notNull(actual, "Actual value must be provided");
        Assert.notNull(expected, "Expected value must be provided");
        Matcher matcher = Pattern.compile(expected.toString()).matcher(actual.toString());
        if (!matcher.find()) {
            this.throwAssertionError("regex '%s' wasn't found in: \n%s", expected, actual);
        }
    }
}
