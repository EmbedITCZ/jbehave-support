package org.jbehavesupport.test;

import java.util.Collections;
import java.util.List;

import org.jbehavesupport.core.AbstractSpringStories;

public class GenericStory extends AbstractSpringStories {

    public static String STORY_FILE = "";

    @Override
    protected List<String> storyPaths() {
        return Collections.singletonList(STORY_FILE);
    }
}
