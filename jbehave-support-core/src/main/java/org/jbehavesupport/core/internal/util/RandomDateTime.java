package org.jbehavesupport.core.internal.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang3.RandomUtils;

public class RandomDateTime {

    public static final RandomDateTime DEFAULT_RANDOM_DATE_TIME = new RandomDateTime(RandomDate.DEFAULT_RANDOM_DATE);

    private final RandomDate randomDate;

    public static RandomDateTimeBuilder builder() {
        return new RandomDateTimeBuilder();
    }

    private RandomDateTime(RandomDate randomDate) {
        this.randomDate = randomDate;
    }

    public LocalDateTime get() {
        LocalDate date = randomDate.get();
        int hour = RandomUtils.nextInt(0, 24);
        int minute = RandomUtils.nextInt(0, 60);
        int second = RandomUtils.nextInt(0, 60);
        LocalTime time = LocalTime.of(hour, minute, second);

        return LocalDateTime.of(date, time);
    }

    public static class RandomDateTimeBuilder {

        private final RandomDate.RandomDateBuilder randomDateBuilder;

        private RandomDateTimeBuilder() {
            this.randomDateBuilder = new RandomDate.RandomDateBuilder();
        }

        public RandomDateTimeBuilder since(LocalDate since) {
            randomDateBuilder.since(since);
            return this;
        }

        public RandomDateTimeBuilder to(LocalDate to) {
            randomDateBuilder.to(to);
            return this;
        }

        public RandomDateTime build() {
            return new RandomDateTime(randomDateBuilder.build());
        }
    }
}

