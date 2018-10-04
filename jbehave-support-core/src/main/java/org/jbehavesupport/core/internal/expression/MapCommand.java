package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.util.regex.Pattern;

import org.jbehavesupport.core.expression.ExpressionCommand;

import org.springframework.stereotype.Component;

@Component
public class MapCommand implements ExpressionCommand {

    private static final Pattern mapPattern = Pattern.compile("^(\\[[^\\]]*\\,[^\\]]*\\])((\\,\\[[^\\]]*\\,[^\\]]*\\])*)(\\,\\[[^\\]]*\\])?$");

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 2, "Two parameters were expected");
        isInstanceOf(String.class, params[0], "First parameter must be string");
        isInstanceOf(String.class, params[1], "Second parameter must be string");

        String value = (String) params[0];
        String map = (String) params[1];
        isTrue(mapPattern.matcher(map).matches(), "Pattern of '" + map + "' doesn't match expected pattern");

        String[] groups = map.split("\\[");
        for (int i = 1; i < groups.length; i++) {
            if (groups[i].contains(",")) {
                String[] mapping = groups[i].substring(0, groups[i].indexOf(']')).split("\\,", -1);
                if (value.equals(mapping[0])) {
                    return mapping[1];
                }
            } else {
                return groups[i].substring(0, groups[i].indexOf(']'));
            }
        }

        throw new IllegalArgumentException("value '" + value + "' not found in mapping '" + map + "'");
    }
}
