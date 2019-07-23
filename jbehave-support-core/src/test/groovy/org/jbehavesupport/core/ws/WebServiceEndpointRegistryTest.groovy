package org.jbehavesupport.core.ws

import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.jbehavesupport.core.test.app.oxm.NameResponse
import spock.lang.Shared
import spock.lang.Specification

import static groovy.test.GroovyAssert.shouldFail

class WebServiceEndpointRegistryTest extends Specification {

    @Shared
    def registry

    void setup() {
        registry = new WebServiceEndpointRegistry()
    }

    def "Register"() {
        when:
        registry.register(NameRequest.class, NameResponse.class)

        then:
        registry.validateRequest("NameRequest")
        registry.validateResponse("NameResponse")
    }

    def "RegisterWithAlias"() {
        when:
        registry.register(NameRequest.class, "callName", NameResponse.class, "getPerson")

        then:
        registry.validateRequest("callName")
        registry.validateResponse("getPerson")
    }

    def "ValidateRequest"() {
        expect:
        String message = shouldFail(IllegalArgumentException.class) {
            registry.validateRequest("requestThatMustNotExist")
        }
        message.contains("Unknown request name")
    }

    def "ValidateResponse"() {
        expect:
        String message = shouldFail(IllegalArgumentException.class) {
            registry.validateResponse("responseToNonExistingRequest")
        }
        message.contains("Unknown response name")
    }

    def "ValidateRequestOrResponse"() {
        expect:
        String message = shouldFail(IllegalArgumentException.class) {
            registry.validateRequestOrResponse("requestyResponsiveObject")
        }
        message.contains("Unknown request or response name")
    }

    def "ResolveEndpoint"() {
        setup:
        registry.register(NameRequest.class, "callName", NameResponse.class, "getPerson")

        when:
        def endpoint = registry.resolveEndpoint(input)

        then:
        endpoint.getRequestClass() == expectedRequestClass
        endpoint.getResponseClass() == expectedResponseClass

        where:
        input       || expectedRequestClass | expectedResponseClass
        "callName"  || NameRequest.class    | NameResponse.class
        "getPerson" || NameRequest.class    | NameResponse.class
    }

    def "EndpointsClasses"() {
        when:
        registry.register(NameRequest.class, NameResponse.class)

        then:
        registry.endpointsClasses().length == 2
    }

    def "refuseSimpleNameWhenAlias"() {
        setup:
        registry.register(NameRequest.class, "callName", NameResponse.class, "getPerson")

        expect:
        registry.resolveEndpoint("NameRequest") == null
    }
}
