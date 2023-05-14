package org.jbehavesupport.core.internal

import org.jbehave.core.model.Scenario
import org.jbehave.core.model.Story
import org.jbehavesupport.core.TestContext
import spock.lang.Shared
import spock.lang.Specification

import static org.jbehavesupport.core.AbstractSpringStories.JBEHAVE_SCENARIO

class FullScenarioNameStorerTest extends Specification {

    @Shared
    FullScenarioNameStorer storer

    @Shared
    TestContext ctx

    def setup() {
        ctx = new TestContextImpl()
        storer = new FullScenarioNameStorer(ctx)
    }

    def "before story and scenario positive"() {
        when:
        storer.beforeStory(new Story("storyPath"), false)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        then:
        ctx.get(JBEHAVE_SCENARIO) == "storyPath#scenarioTitle"
    }

    def "before and after story positive"() {
        expect:
        storer.beforeStory(new Story("storyPath"), false)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        ctx.get(JBEHAVE_SCENARIO) == "storyPath#scenarioTitle"

        storer.afterStory(false)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        ctx.get(JBEHAVE_SCENARIO) == "null#scenarioTitle"
    }

    def "before story and scenario negative "() {
        when:
        storer.beforeStory(new Story("storyPath"), true)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        then:
        ctx.get(JBEHAVE_SCENARIO) == "null#scenarioTitle"
    }

    def "before and after story"() {
        expect:
        storer.beforeStory(new Story("storyPath"), false)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        ctx.get(JBEHAVE_SCENARIO) == "storyPath#scenarioTitle"

        storer.afterStory(true)
        storer.beforeScenario(new Scenario("scenarioTitle", null))

        ctx.get(JBEHAVE_SCENARIO) == "storyPath#scenarioTitle"
    }

}
