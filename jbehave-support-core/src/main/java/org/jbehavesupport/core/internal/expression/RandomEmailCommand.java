package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.util.Random;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.RandomGeneratorHelper;

import org.springframework.stereotype.Component;

/**
 * Generate random email.
 */
@Component
public class RandomEmailCommand implements ExpressionCommand {

    private static final String[] DOMAINS = new String[]{"com", "gov", "org", "cz", "sk", "uk"};

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 0, "No parameters expected for this command.");
        return String.format("%s@%s.%s", RandomGeneratorHelper.randomAlphabetic(20),
            RandomGeneratorHelper.randomAlphabetic(10),
            DOMAINS[new Random().nextInt(DOMAINS.length)]);
    }
}
