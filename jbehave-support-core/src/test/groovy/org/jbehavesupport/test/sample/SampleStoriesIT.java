package org.jbehavesupport.test.sample;

import java.util.Arrays;
import java.util.List;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.TestConfig;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestConfig.class)
public class SampleStoriesIT extends AbstractSpringStories {

    @Override
    protected List<String> storyPaths() {
        return Arrays.asList(
            "org/jbehavesupport/test/sample/Sql.story",
            "org/jbehavesupport/test/sample/WebService.story",
            "org/jbehavesupport/test/sample/WebServiceFail.story",
            "org/jbehavesupport/test/sample/Rest.story",
            "org/jbehavesupport/test/sample/Web.story",
            "org/jbehavesupport/test/sample/WebGivenStoryUsage.story",
            "org/jbehavesupport/test/sample/WebProperty.story",
            "org/jbehavesupport/test/sample/WebWaitCondition.story",
            "org/jbehavesupport/test/sample/WebAction.story",
            "org/jbehavesupport/test/sample/WebNavigation.story",
            "org/jbehavesupport/test/sample/Ssh.story",
            "org/jbehavesupport/test/sample/SshReport.story",
            "org/jbehavesupport/test/sample/HealthCheck.story",
            "org/jbehavesupport/test/sample/Jms.story",
            "org/jbehavesupport/test/sample/Verification.story"
        );
    }
}
