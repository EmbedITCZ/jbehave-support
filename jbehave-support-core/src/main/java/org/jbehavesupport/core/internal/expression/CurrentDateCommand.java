package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.time.LocalDate;
import java.time.Period;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.support.TimeFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The CurrentDateCommand can be used in JBehave's tables in three ways:
 *
 * <ul>
 *     <li><code>{CURRENT_DATE}</code> without parameter, is evaluated to the current date based on {@link TimeFacade}</li>
 *     <li><code>{CURRENT_DATE:&lt;number&gt;}</code> with numeric parameter, is evaluated to the current date shifted about given number of days</li>
 *     <li><code>{CURRENT_DATE:&lt;period&gt;}</code> with period parameter, is evaluated to the current day shifted about given period of time, see {@link Period#parse}</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class CurrentDateCommand implements ExpressionCommand {

    private final TimeFacade timeFacade;

    @Override
    public LocalDate execute(Object... params) {
        isTrue(params.length < 2, "None or one parameter is expected");

        LocalDate result;
        LocalDate currentDate = timeFacade.getCurrentLocalDate();

        if (params.length == 0) {
            result = currentDate;
        } else {
            String param = params[0].toString();
            if (param.startsWith("P")) {
                Period period = Period.parse(param);
                result = currentDate.plus(period);
            } else {
                int days = Integer.parseInt(param);
                result = currentDate.plusDays(days);
            }
        }
        return result;
    }
}
