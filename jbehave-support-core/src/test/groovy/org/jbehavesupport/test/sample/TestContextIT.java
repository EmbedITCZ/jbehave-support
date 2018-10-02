package org.jbehavesupport.test.sample;

import java.util.Arrays;
import java.util.List;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.TestConfig;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestConfig.class)
public class TestContextIT extends AbstractSpringStories {

    @Override
    protected List<String> storyPaths() {
        return Arrays.asList(
            "org/jbehavesupport/test/sample/EscapingInContext.story",
            "org/jbehavesupport/test/sample/LoadContextDataFromFile.story"
        );
    }
}
