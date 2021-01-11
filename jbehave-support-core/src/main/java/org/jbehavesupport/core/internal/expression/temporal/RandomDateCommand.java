package org.jbehavesupport.core.internal.expression.temporal;


import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.StringUtils.hasText;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.internal.util.RandomDate;
import org.springframework.stereotype.Component;

/**
 * Generate random {@link java.time.LocalDate}
 * <ul>
 *     <li>Optional first parameter can specify start period in following formats: YYYY | YYYY-MM | YYYY-MM-DD defaulting missing parts to 1</li>
 *     <li>Optional second parameter can specify end period in following formats: YYYY | YYYY-MM | YYYY-MM-DD defaulting missing parts to 1</li>
 * </ul>
 */
@Component
public class RandomDateCommand implements ExpressionCommand {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy[-M[-d]]")
        .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
        .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
        .toFormatter();

    @Override
    public Object execute(Object... params) {
        isTrue(params.length <= 2, "At most 2 parameters can be provided");
        LocalDate since = params.length >= 1 ? parseTime((String) params[0]) : null;
        LocalDate to = params.length == 2 ? parseTime((String) params[1]) : null;

        return RandomDate.builder()
            .since(since)
            .to(to)
            .build()
            .get();
    }

    LocalDate parseTime(String input) {
        if (!hasText(input)) {
            return null;
        }
        return DATE_TIME_FORMATTER.parse(input, LocalDate::from);
    }
}
