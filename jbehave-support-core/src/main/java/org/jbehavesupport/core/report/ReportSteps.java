package org.jbehavesupport.core.report;

import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.report.extension.ServerLogXmlReporterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static org.junit.Assert.assertNotNull;

@Slf4j
@Component
public class ReportSteps {

    @Autowired(required = false)
    private ServerLogXmlReporterExtension serverLogXmlReporterExtension;

    @Given("ssh reporter mode is set to [$mode]")
    public void setSshReporterMode(ExpressionEvaluatingParameter<String> mode) {
        assertNotNull("serverLogXmlReporterExtension is not registered", serverLogXmlReporterExtension);
    }
}
