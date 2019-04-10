package org.jbehavesupport.core.internal.util;

import java.time.LocalDate;

import lombok.Builder;
import org.apache.commons.lang3.RandomUtils;

@Builder
public class RandomDate {

    private static final LocalDate DEFAULT_SINCE = LocalDate.of(1970, 1, 1);
    private static final LocalDate DEFAULT_TO = LocalDate.of(2050, 12, 31);

    public static final RandomDate DEFAULT_RANDOM_DATE = RandomDate.builder()
        .since(DEFAULT_SINCE)
        .to(DEFAULT_TO)
        .build();


    public static RandomDateBuilder builder() {
        return new RandomDateBuilder();
    }

    private final LocalDate since;
    private final LocalDate to;

    public LocalDate get() {
        long minDay = since.toEpochDay();
        long maxDay = to.toEpochDay();
        long randomDay = RandomUtils.nextInt((int) minDay, (int) maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static class RandomDateBuilder {

        public RandomDateBuilder since(LocalDate since) {
            this.since = since != null ? since : DEFAULT_SINCE;
            return this;
        }

        public RandomDateBuilder to(LocalDate to) {
            this.to = to != null ? to : DEFAULT_TO;
            return this;
        }
    }
}
