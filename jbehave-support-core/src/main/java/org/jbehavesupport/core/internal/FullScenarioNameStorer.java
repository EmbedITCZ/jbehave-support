package org.jbehavesupport.core.internal;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.model.Scenario;
import org.jbehavesupport.core.TestContext;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehavesupport.core.AbstractSpringStories;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FullScenarioNameStorer extends NullStoryReporter {

    private static final ThreadLocal<String> CURRENT_STORY = new ThreadLocal<>();

    private final TestContext testContext;

    @Override
    public void beforeStory(Story story, boolean givenStory) {
        if (!givenStory) {
            CURRENT_STORY.set(StringUtils.substringBefore(story.getName(), ".story"));
        }
    }

    @Override
    public void beforeScenario(Scenario scenario) {
        testContext.put(AbstractSpringStories.JBEHAVE_SCENARIO, String.format("%s#%s", CURRENT_STORY.get(), scenario.getTitle()));
    }

    @Override
    public void afterStory(boolean givenStory) {
        if (!givenStory) {
            CURRENT_STORY.remove();
        }
    }

}
