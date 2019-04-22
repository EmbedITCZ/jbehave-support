package org.jbehavesupport.core.internal.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.springframework.util.Assert.notNull;

@UtilityClass
public class ArrayComparator {

    public static boolean arraysEquals(Object o1, Object o2) {
        notNull(o1, "First value value must be provided");
        notNull(o2, "Second value value must be provided");
        assertTrue("First value must be array", o1.getClass().isArray());
        assertTrue("Second value must be array", o2.getClass().isArray());

        if (o1.getClass().getComponentType().isPrimitive()) {
            return comparePrimitiveArrays(o1, o2);
        } else {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }

    }

    private static boolean comparePrimitiveArrays(Object o1, Object o2) {
        Class<?> componentType = o1.getClass().getComponentType();
        if (!componentType.equals(o2.getClass().getComponentType())) {
            return false;
        }

        if (componentType.equals(byte.class)) {
            return (Arrays.equals((byte[]) o1, (byte[]) o2));
        }

        if (componentType.equals(short.class)) {
            return (Arrays.equals((short[]) o1, (short[]) o2));
        }

        if (componentType.equals(char.class)) {
            return (Arrays.equals((char[]) o1, (char[]) o2));
        }

        if (componentType.equals(int.class)) {
            return (Arrays.equals((int[]) o1, (int[]) o2));
        }

        if (componentType.equals(long.class)) {
            return (Arrays.equals((long[]) o1, (long[]) o2));
        }

        if (componentType.equals(float.class)) {
            return (Arrays.equals((float[]) o1, (float[]) o2));
        }

        if (componentType.equals(double.class)) {
            return (Arrays.equals((double[]) o1, (double[]) o2));
        }

        if (componentType.equals(boolean.class)) {
            return (Arrays.equals((boolean[]) o1, (boolean[]) o2));
        }

        return false;
    }

}
