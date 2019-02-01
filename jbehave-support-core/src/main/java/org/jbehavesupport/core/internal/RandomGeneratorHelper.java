package org.jbehavesupport.core.internal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

/**
 * The type Random generator helper.
 */
public final class RandomGeneratorHelper {

    private RandomGeneratorHelper() {
        throw new UnsupportedOperationException();
    }

    /**
     * Random numeric string.
     *
     * @param count the length of random string to create
     * @return the string
     */
    public static String randomNumericAsString(int count) {
        String randomNumeric = RandomStringUtils.randomNumeric(count);
        if (randomNumeric.startsWith("0")) {
            randomNumeric = "1" + randomNumeric.substring(1);
        }
        return randomNumeric;
    }

    /**
     * Random alphabetic string.
     *
     * @param count the count
     * @return the string
     */
    public static String randomAlphabetic(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static LocalDate randomDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2050, 12, 31).toEpochDay();
        long randomDay = RandomUtils.nextInt((int) minDay, (int) maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static LocalDateTime randomDateTime() {
        LocalDate date = randomDate();
        int hour = RandomUtils.nextInt(0, 24);
        int minute = RandomUtils.nextInt(0, 60);
        int second = RandomUtils.nextInt(0, 60);
        LocalTime time = LocalTime.of(hour, minute, second);

        return LocalDateTime.of(date, time);
    }

}
