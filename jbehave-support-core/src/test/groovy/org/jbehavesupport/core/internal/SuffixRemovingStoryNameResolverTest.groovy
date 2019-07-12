package org.jbehavesupport.core.internal

import spock.lang.Shared
import spock.lang.Specification

class SuffixRemovingStoryNameResolverTest extends Specification {

    @Shared
    def resolver

    void setup() {
        resolver = new SuffixRemovingStoryNameResolver()
    }

    def "Resolve name"() {

        when:
        def name = resolver.resolveName(path)

        then:
        name == expected

        where:
        path                   || expected
        "my"                   || "my"
        "my.story"             || "my"
        "/my.story"            || "my"
        "//my.story"           || "my"
        "/this/my.story"       || "my"
        "only/this/my.story"   || "my"
        "only///this/my.story" || "my"
        ".my.story"            || "my"
        "this.my.story"        || "my"
        "..this.my.story"      || "my"
        "..this/.my.story"     || "my"
        "..//this.my.story"    || "my"
        "MyPrettyStory"        || "My Pretty Story"
        "MyPrettyStory.story"  || "My Pretty Story"
        ""                     || ""
    }

    def "Resolve name negative"() {

        when:
        resolver.resolveName(null)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Provided path must not be null"
    }
}
