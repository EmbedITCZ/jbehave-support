package org.jbehavesupport.test.sample.report;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.SshContainer;
import org.jbehavesupport.test.support.TestConfig;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = TestConfig.class)
public class ReportSampleStoriesIT extends AbstractSpringStories {

    static {
        SshContainer.getInstance().start();
    }

    @Override
    protected List<String> storyPaths() {
        return Arrays.asList(
            "org/jbehavesupport/test/sample/report/Report.story",
            "org/jbehavesupport/test/sample/report/SshReport.story"
        );
    }
}
