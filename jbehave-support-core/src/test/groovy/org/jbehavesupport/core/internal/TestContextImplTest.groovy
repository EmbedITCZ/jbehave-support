package org.jbehavesupport.core.internal

import spock.lang.Specification
import spock.lang.Unroll

import org.jbehavesupport.core.TestContext

@Unroll
class TestContextImplTest extends Specification {

    def "Get values with type #key and expected #expected"() {
        setup:
        TestContext tc = new TestContextImpl()
        tc.put("myLong", new Long(7))
        tc.put("myBigInt", new BigInteger(8))
        tc.put("myBigDec", new BigDecimal("9"))
        tc.put("stLong", "22")
        tc.put("stBigInt", "33")
        tc.put("stBigDec", "44")

        when:
        Object resp = tc.get(key)

        then:
        resp == expected

        where:
        key        | expected
        "myLong"   | Long.valueOf(7)
        "myBigInt" | new BigInteger(8)
        "myBigDec" | new BigDecimal(9)
        "stLong"   | "22"
        "stBigInt" | "33"
        "stBigDec" | "44"
    }

    def "isReferenceKey #key expected: #expected"() {
        setup:
        TestContext tc = new TestContextImpl()

        when:
        def resp = tc.isReferenceKey(key)

        then:
        resp == expected

        where:
        key              || expected
        "*_reference_1"  || true
        "*_reference_1s" || false
        "_reference_1"   || false
        "1*_reference_1" || false
        null             || false
    }
}
