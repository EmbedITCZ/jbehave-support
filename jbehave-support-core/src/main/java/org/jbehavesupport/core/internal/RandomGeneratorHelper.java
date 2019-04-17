package org.jbehavesupport.core.internal;

import org.apache.commons.lang3.RandomStringUtils;

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

}
