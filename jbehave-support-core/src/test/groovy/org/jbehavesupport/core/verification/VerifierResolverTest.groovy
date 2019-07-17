package org.jbehavesupport.core.verification

import org.jbehavesupport.core.TestConfig
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class VerifierResolverTest extends Specification {

    @Autowired
    VerifierResolver verifierResolver

    def "GetVerifierByNamePositive"() {

        expect:
        verifierResolver.getVerifierByName(VerifierNames.EQ) != null
    }

    def "GetVerifierByNameNegative"() {

        when:
        verifierResolver.getVerifierByName("MySecretVerifier")

        then:
        def e = thrown(NoSuchBeanDefinitionException.class)
        e.getMessage() == "No bean named 'MySecretVerifier' available"

    }

    def "GetMultipleVerifiersNegative"() {

        when:
        verifierResolver.getVerifierByName(VerifierNames.REGEX_MATCH)

        then:
        def e = thrown(NoUniqueBeanDefinitionException.class)
        e.getMessage().contains("expected single matching bean")
    }

}
