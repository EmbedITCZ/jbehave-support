package org.jbehavesupport.test.support;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import org.jbehavesupport.core.test.app.oxm.NameRequest;
import org.jbehavesupport.core.test.app.oxm.NameResponse;
import org.jbehavesupport.core.ws.WebServiceEndpointRegistry;
import org.jbehavesupport.core.ws.WebServiceHandler;
import org.jbehavesupport.core.ws.WebServiceTemplateConfigurer;

import org.jbehave.core.model.ExamplesTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class TestWebServiceHandler extends WebServiceHandler {

    @Autowired
    private Environment env;

    @Override
    protected void initEndpoints(WebServiceEndpointRegistry endpointRegistry) {
        endpointRegistry
            .register(NameRequest.class, NameResponse.class)
            .register(NameRequest.class, "AliasNameRequest", NameResponse.class, "AliasNameResponse");
    }

    @Override
    protected void initTemplate(WebServiceTemplateConfigurer templateConfigurer) {
        templateConfigurer
            .defaultUri(env.getProperty("ws.url"));
    }

    @Override
    protected String getSuccessResult() {
        return "| code |\n" +
               "| OK   |";
    }

    @Override
    protected void verifyResults(final Object response, final ExamplesTable expectedResults) {
        super.verifyResults(response, expectedResults);
        if (response instanceof NameResponse && nonNull(((NameResponse) response).getError())) {
            assertThat(((NameResponse) response).getError().getCode())
                .isEqualTo(expectedResults.getRows().get(0).get("code"));
        }
    }
}
