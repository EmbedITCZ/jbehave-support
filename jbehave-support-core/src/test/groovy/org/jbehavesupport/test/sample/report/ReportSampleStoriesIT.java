package org.jbehavesupport.test.sample.report;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.SshContainer;
import org.jbehavesupport.test.support.TestConfig;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

// spring junit4 runner needed for testcontainer lifecycle by classrule to work
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ReportSampleStoriesIT extends AbstractSpringStories {

    @ClassRule
    public static SshContainer sshContainer = new SshContainer();

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
