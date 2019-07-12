package org.jbehavesupport.core.rest

import org.jbehave.core.model.ExamplesTable
import org.junit.Test

import static groovy.test.GroovyAssert.shouldFail

class RestTest {

    private RestServiceHandler restServiceHandler = new RestServiceHandler("http://")

    @Test
    void shouldRejectMultipleKeysWhenBody() {
        ExamplesTable examplesTable = new ExamplesTable(
            "| name      | data             |\n" +
            "| person.id | 22               |\n" +
            "| @body     | this is raw body |")

        String message = shouldFail(IllegalStateException.class) {
            restServiceHandler.sendRequest("test", null, examplesTable)
        }
        assert message.contains("If @body is present, no other keys except headers is allowed.")
    }
}
