package org.jbehavesupport.core.internal.expression.temporal;

import static org.springframework.util.Assert.isTrue;

import java.time.LocalDateTime;
import java.time.Period;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.support.TimeFacade;
import org.springframework.stereotype.Component;

/**
 * The CurrentDateTimeCommand can be used in JBehave's tables in three ways:
 *
 * <ul>
 * <li><code>{CURRENT_DATE_TIME}</code> without parameter, is evaluated to the current date based on {@link TimeFacade}</li>
 * <li><code>{CURRENT_DATE_TIME:&lt;number&gt;}</code> with numeric parameter, is evaluated to the current date shifted about given number of seconds</li>
 * <li><code>{CURRENT_DATE_TIME:&lt;period&gt;}</code> with period parameter, is evaluated to the current date shifted about given period of time, see {@link Period#parse}</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class CurrentDateTimeCommand implements ExpressionCommand {

    private final TimeFacade timeFacade;

    @Override
    public LocalDateTime execute(Object... params) {
        isTrue(params.length < 2, "None or one parameter is expected");

        LocalDateTime result;
        LocalDateTime currentDate = timeFacade.getCurrentLocalDateTime();

        if (params.length == 0) {
            result = currentDate;
        } else {
            String param = params[0].toString();
            if (param.startsWith("P")) {
                Period period = Period.parse(param);
                result = currentDate.plus(period);
            } else {
                int seconds = Integer.parseInt(param);
                result = currentDate.plusSeconds(seconds);
            }
        }
        return result;
    }
}
