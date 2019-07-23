package org.jbehavesupport.core.internal

import spock.lang.Specification
import spock.lang.Unroll

class ReflectionUtilsTest extends Specification {

    @Unroll
    def "GetPropertyValue for path #propertyPath expecting #expect"() {
        setup:
        Boo b = new Boo("myBB", null, ["liB"] as Set)
        Foo f = new Foo(b, "FFs", 467, ["LiL", "Pyl"])

        when:
        def result = ReflectionUtils.getPropertyValue(f, propertyPath)

        then:
        result == expect

        where:
        propertyPath      || expect
        "fooString"       || "FFs"
        "fooInteger"      || 467
        "stringList.0"    || "LiL"
        "stringList.1"    || "Pyl"
        "boo.booString"   || "myBB"
        "boo.booInteger"  || null
        "boo.stringSet.0" || "liB"
    }


    def "GetPropertyValueNegative"() {
        setup:
        Foo f = new Foo(null, null, null, null)

        when:
        ReflectionUtils.getPropertyValue(f, propertyPath)

        then:
        def exception = thrown(Throwable)
        exception.getMessage() == exceptionMessage

        where:
        propertyPath    || exceptionMessage
        "badPath"       || "Requested field from path badPath probably does not exist, or isn't set"
        "boo.string"    || "Requested field from path boo.string probably does not exist, or isn't set"
        null            || "property path must be specified"
        ""              || "property path is empty"
        "boo.booString" || "Requested field from path boo.booString probably does not exist, or isn't set"
    }


    static class Boo {
        String booString
        Integer booInteger
        Set<String> stringSet

        Boo(String booString, Integer booInteger, Set<String> stringSet) {
            this.booString = booString
            this.booInteger = booInteger
            this.stringSet = stringSet
        }
    }

    static class Foo {
        Boo boo
        String fooString
        Integer fooInteger
        List<String> stringList

        Foo(Boo boo, String fooString, Integer fooInteger, List<String> stringList) {
            this.boo = boo
            this.fooString = fooString
            this.fooInteger = fooInteger
            this.stringList = stringList
        }
    }


}
