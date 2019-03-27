package org.jbehavesupport.core.web

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class ByFactoryResolverTest extends Specification {

    @Autowired
    ByFactoryResolver byFactoryResolver;

    def "GetByCreatorByTypePositive"() {

        expect:
        byFactoryResolver.resolveByFactory("css") != null;
    }

    def "GetByCreatorByTypeNegative"() {

        when:
        byFactoryResolver.resolveByFactory("MySecretBy");

        then:
        def e = thrown(IllegalArgumentException.class)
        e.getMessage() == "No ByFactory found for given name [MySecretBy]."

    }

    def "GetMultipleByCreatorsNegative"() {

        when:
        byFactoryResolver.resolveByFactory("xpath");

        then:
        def e = thrown(NoUniqueBeanDefinitionException.class)
        e.getMessage().contains("expected single matching bean")
    }

}
