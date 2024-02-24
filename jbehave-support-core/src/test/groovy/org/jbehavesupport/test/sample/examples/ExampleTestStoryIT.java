package org.jbehavesupport.test.sample.examples;

import org.jbehavesupport.core.AbstractSpringStories;

import java.util.List;

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
