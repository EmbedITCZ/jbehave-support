package org.jbehavesupport.test.sample;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.SshContainer;
import org.jbehavesupport.test.support.TestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = TestConfig.class)
public class SampleStoriesIT extends AbstractSpringStories {

    public static SshContainer sshContainer = new SshContainer();

    static {
        sshContainer.start();
    }

    @DynamicPropertySource
    static void sshProperties(DynamicPropertyRegistry registry) {
        sshContainer.updateDynamicPropertyRegistry(registry);
    }

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
            "org/jbehavesupport/test/sample/WebTable.story",
            "org/jbehavesupport/test/sample/Ssh.story",
            "org/jbehavesupport/test/sample/HealthCheck.story",
            "org/jbehavesupport/test/sample/Verification.story",
            "org/jbehavesupport/test/sample/Command.story"
        );
    }
}
