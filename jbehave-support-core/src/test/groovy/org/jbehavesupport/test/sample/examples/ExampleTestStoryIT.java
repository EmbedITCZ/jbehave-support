package org.jbehavesupport.test.sample.examples;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.TestConfig;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

@ContextConfiguration(classes = TestConfig.class)
public class ExampleTestStoryIT extends AbstractSpringStories {

    @Override
    protected List<String> storyPaths() {
        return List.of(
                "org/jbehavesupport/test/sample/examples/ExampleTest.story",
                "org/jbehavesupport/test/sample/examples/ExampleTestFromFile.story",
                "org/jbehavesupport/test/sample/examples/ExamplesInExamples.story"
        );
    }
}
