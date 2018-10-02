package org.jbehavesupport.core.util

import org.jbehavesupport.core.internal.RandomGeneratorHelper
import spock.lang.Specification

class RandomGeneratorHelperTest extends Specification {

    def "randomNumericAsString"() {

        expect:
        for (int i = 0; i < 30; i++) {
            RandomGeneratorHelper.randomNumericAsString(i).length() == i
        }
    }
}
