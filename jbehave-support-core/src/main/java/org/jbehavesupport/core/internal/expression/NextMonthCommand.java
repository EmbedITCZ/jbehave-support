package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.time.LocalDate;
import java.time.Period;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.support.TimeFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The NextMonthCommand can be used in JBehave's tables in three ways:
 * <ul>
 *     <li><code>{NEXT_MONTH}</code> without parameter, is evaluated to the first day of the next month</li>
 *     <li><code>{NEXT_MONTH:&lt;number&gt;}</code> with numeric parameter, is evaluated to the first day of the next month shifted about given number of days</li>
 *     <li><code>{NEXT_MONTH:&lt;period&gt;}</code> with period parameter, is evaluated to the first day of the next month shifted about given period of time, see {@link Period#parse}</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class NextMonthCommand implements ExpressionCommand {

    private final TimeFacade timeFacade;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length < 2, "Only zero or one is expected");

        LocalDate currentDay = timeFacade.getCurrentLocalDate();
        LocalDate firstDayNextMonth = currentDay.plusMonths(1).withDayOfMonth(1);

        if (params.length == 0) {
            return firstDayNextMonth;
        }

        String param = params[0].toString();
        if (param.startsWith("P")) {
            Period periodShift = Period.parse(param);
            return firstDayNextMonth.plus(periodShift);
        } else {
            int dayOfMonth = Integer.parseInt(param);
            return firstDayNextMonth.withDayOfMonth(dayOfMonth);
        }
    }

}
