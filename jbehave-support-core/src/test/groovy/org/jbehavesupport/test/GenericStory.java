package org.jbehavesupport.test;

import java.util.Collections;
import java.util.List;

import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.test.support.TestConfig;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestConfig.class)
public class GenericStory extends AbstractSpringStories {

    public static String STORY_FILE = "";

    @Override
    protected List<String> storyPaths() {
        return Collections.singletonList(STORY_FILE);
    }
}
