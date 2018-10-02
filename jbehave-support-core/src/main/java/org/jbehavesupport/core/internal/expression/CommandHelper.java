package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommandHelper {

    private static final String SPLITTER = "(?<!\\\\):";
    private static final String KEEPER = "'";

    /**
     * Extract command name from expression.
     * @param expression the expression
     * @return the string
     */
    public static String commandName(@NonNull String expression) {
        String[] expressionParts = expression.split(SPLITTER);
        if (expressionParts.length > 0) {
            return expressionParts[0];
        } else {
            throw new IllegalArgumentException("Expression not properly set: " + expression);
        }
    }

    /**
     * Extract command params from expression.
     * @param expression the expression
     * @return the string [ ]
     */
    public static String[] commandParams(@NonNull String expression) {
        isTrue(!expression.trim().isEmpty(), "Expression not properly set: " + expression);
        List<String> commandParts = new ArrayList<>();
        String[] solidParts = expression.split(KEEPER);
        for (int i = 0; i < solidParts.length; ++i) {
            String solidPart = solidParts[i].replaceFirst("^:", "");
            if (!solidPart.isEmpty()) {
                if (i % 2 == 0) {
                    commandParts.addAll(Arrays.asList(solidPart.split(SPLITTER)));
                } else {
                    commandParts.add(solidPart);
                }
            }
        }
        commandParts.remove(commandName(expression));

        if (!commandParts.isEmpty()) {
            return commandParts.toArray(new String[]{});
        }
        return new String[]{};
    }

}
