package org.jbehavesupport.core.internal.util

import spock.lang.Specification
import spock.lang.Unroll

class ArrayComparatorTest extends Specification {

    @Unroll
    def "comparison of primitive arrays #first and #second is #expected"() {
        when:
        def result = ArrayComparator.arraysEquals(first, second)

        then:
        result == expected

        where:
        first                       | second                        | expected
        [0, 1] as int[]             | [1, 0]  as int[]              | false
        [0, 1] as int[]             | [0, 1]  as int[]              | true
        [0L, 1L] as long[]          | [1L, 0L] as long[]            | false
        [0L, 1L] as long[]          | [0L, 1L] as long[]            | true
        [true, false] as boolean[]  | [false, true] as boolean[]    | false
        [true, false] as boolean[]  | [true, false] as boolean[]    | true
        [0d, 1d] as double[]        | [1d, 0d] as double[]          | false
        [0d, 1d] as double[]        | [0d, 1d] as double[]          | true
        [0d, 1d] as float[]         | [1d, 0d] as float[]           | false
        [0d, 1d] as float[]         | [0d, 1d] as float[]           | true
        [0x0, 0x1] as char[]        | [0x1, 0x0]  as char[]         | false
        [0x0, 0x1] as char[]        | [0x0, 0x1]  as char[]         | true
        [0x0, 0x1] as byte[]        | [0x1, 0x0]  as byte[]         | false
        [0x0, 0x1] as byte[]        | [0x0, 0x1]  as byte[]         | true
        [0, 1] as short[]           | [1, 0]  as short[]            | false
        [0, 1] as short[]           | [0, 1]  as short[]            | true
        [0, 1] as int[]             | [1, 0]  as char[]             | false
        ["0", "1"] as String[]      | ["0", "1"] as String[]        | true
    }

}
