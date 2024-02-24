package org.jbehavesupport.core.ssh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.verification.ContainsVerifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public final class SshSteps {

    private final SshHandler sshHandler;
    private final ContainsVerifier containsVerifier;

    @Given("current time is saved as log timestamp [$logTimeAlias]")
    public void markLogTime(String logTimeAlias) {
        sshHandler.markLogTime(logTimeAlias);
    }

    @Given("log start timestamp is set to current time")
    public void saveLogStartTime() {
        sshHandler.saveLogStartTime();
    }

    @Given("log end timestamp is set to current time")
        public void saveLogEndTime() {
        sshHandler.saveLogEndTime();
    }

    @Given("log start timestamp is set to [$contextAlias]")
    public void saveLogStartTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        sshHandler.setLogStartTimeOnSaved(contextAlias);
    }

    @Given("log end timestamp is set to [$contextAlias]")
    public void setLogEndTimeOnSaved(ExpressionEvaluatingParameter<String> contextAlias) {
        sshHandler.setLogEndTimeOnSaved(contextAlias);
    }

    @Then("the following data are present in [$systemQualifier] log:$presentData")
    public void logContainsData(String systemQualifier, String stringTable) {
        sshHandler.checkLogDataPresence(systemQualifier, stringTable, containsVerifier);
    }

}
