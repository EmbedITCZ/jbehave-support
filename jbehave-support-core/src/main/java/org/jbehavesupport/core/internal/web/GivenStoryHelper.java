package org.jbehavesupport.core.internal.web;

import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.springframework.stereotype.Component;

@Component
public class GivenStoryHelper extends NullStoryReporter {

    private int nestedLevel;

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        nestedLevel = ++nestedLevel;
    }

    @Override
    public void afterStory(boolean givenStory) {
        nestedLevel = --nestedLevel;
    }

    public boolean isInGivenStory() {
        return nestedLevel > 1;
    }
}
