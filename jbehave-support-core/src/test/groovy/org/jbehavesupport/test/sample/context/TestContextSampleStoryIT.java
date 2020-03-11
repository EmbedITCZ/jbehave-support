package org.jbehavesupport.test.sample.context;

import java.util.Arrays;
import java.util.List;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.TestConfig;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestConfig.class)
public class TestContextSampleStoryIT extends AbstractSpringStories {

    @Override
    protected List<String> storyPaths() {
        return Arrays.asList(
            "org/jbehavesupport/test/sample/context/EscapingInContext.story",
            "org/jbehavesupport/test/sample/context/LoadContextDataFromFile.story",
            "org/jbehavesupport/test/sample/context/TestContextSample.story"
        );
    }
}
