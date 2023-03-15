package org.jbehavesupport.test.sample.report;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.SshContainer;
import org.jbehavesupport.test.support.TestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = TestConfig.class)
public class ReportSampleStoriesIT extends AbstractSpringStories {

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
            "org/jbehavesupport/test/sample/report/Report.story",
            "org/jbehavesupport/test/sample/report/SshReport.story"
        );
    }
}
