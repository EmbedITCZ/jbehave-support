package org.jbehavesupport.core.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * The interface represents facade for obtaining current date and time.
 * It allows to provide custom time-machine implementation for test harness.
 * By default it use system time.
 */
public interface TimeFacade {

    static TimeFacade getDefault() {
        return () -> Instant.now();
    }

    Instant getCurrentInstant();

    default Date getCurrentDate() {
        return Date.from(getCurrentInstant());
    }

    default LocalDate getCurrentLocalDate() {
        return getCurrentLocalDateTime().toLocalDate();
    }

    default LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.ofInstant(getCurrentInstant(), ZoneId.systemDefault());
    }

}
