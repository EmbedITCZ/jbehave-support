package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.jbehavesupport.core.expression.ExpressionCommand;
import org.jbehavesupport.core.support.TimeFacade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Set day to {@link java.time.LocalDate}.
 * Command consumes one parameter day. If the day is higher or equal to day from {@link TimeFacade} current month is used.
 * If the day is lower than day from {@link TimeFacade} we will reset day to 1 and set month to next month.
 * <p>
 * TimeFacade: 20-5-2005
 * Param: 1, 20, 31
 * Result: 1-6-2005, 20-5-2005, 31-5-2005
 */
@Component
@RequiredArgsConstructor
public class NextCalendarDayCommand implements ExpressionCommand {

    private static final int PROTECTION_PERIOD = 0;

    private final TimeFacade timeFacade;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1, "Illegal number of parameters " + Arrays.toString(params));

        String actualDate = params[0].toString();
        isTrue(actualDate.matches("^\\d+$"), "Parameter must be a number");

        int day = Integer.parseInt(actualDate);
        isTrue(day >= 1 && day <= 31, "A number between 1 and 28 is expected.");

        Date currentDate = timeFacade.getCurrentDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        Calendar returnDate = Calendar.getInstance();
        returnDate.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
        if (day >= currentDayOfMonth + PROTECTION_PERIOD) {
            returnDate.add(Calendar.DATE, day - 1);
        } else {
            returnDate.add(Calendar.MONTH, 1);
            returnDate.add(Calendar.DATE, day - 1);
        }

        // if the date is moved to the next month because the calculated month is too short the day should always be 1
        if (day != returnDate.get(Calendar.DAY_OF_MONTH)) {
            returnDate.set(Calendar.DAY_OF_MONTH, 1);
        }

        Instant instant = returnDate.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.toLocalDate();
    }
}
