package org.jbehavesupport.core.filtering


import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.jbehave.core.embedder.MetaFilter
import org.jbehave.core.model.Story
import org.jbehavesupport.core.parsers.FilteringStoryParser
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 * Test of extended groovy filter and story parser
 */
class GroovyFilteringTest {
    private static final String STORY = new ClassPathResource("org/jbehavesupport/test/filtering/GroovyMetafilter.story")
        .getInputStream().withCloseable { t -> IOUtils.toString(t, "UTF-8") }

    @Test
    void parseStoryWithNameGenerator() {
        def parser = new FilteringStoryParser(null, true)
        def story = parser.parseStory(STORY, null)
        assert story.getName() == ""

        parser = new FilteringStoryParser(null, true, { storyPath -> "Name:" + storyPath })
        story = parser.parseStory(STORY, null)
        assert story.getName() == "Name:"

        parser = new FilteringStoryParser(null, true, { storyPath -> "Name:" + storyPath })
        story = parser.parseStory(STORY, "Path")
        assert story.getName() == "Name:Path"
    }

    @Test
    void parseStoryWithNoFilter() {
        def parser = new FilteringStoryParser(null, true)
        def story = parser.parseStory(STORY, null)
        // Returns all scenarios
        assert contains(story, "001", "002", "003", "004", "005", "006", "007", "008")


        parser = new FilteringStoryParser(filter(), true)
        story = parser.parseStory(STORY, null)
        // Returns all scenarios except ignored and marked as features
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter(null, null, false), true)
        story = parser.parseStory(STORY, null)
        // Returns all scenarios including ignored except marked as features
        assert contains(story, "001", "002", "003", "006", "007", "008")
    }

    @Test
    void filterWithAny() {
        // Scenarios not having searched attribute are included by default, ignored and featured are not included
        def parser = new FilteringStoryParser(filter("any('attribute', 'a1')"), true)
        def story = parser.parseStory(STORY, null)
        // No scenarios with attribute=1
        assert contains(story, "001", "003", "007", "008")

        parser = new FilteringStoryParser(filter("any('attribute', 'a2')"), true)
        story = parser.parseStory(STORY, null)
        // 002
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter("any('inherit', 'i1')"), true)
        story = parser.parseStory(STORY, null)
        // 001, 002, 003
        assert contains(story, "001", "002", "003")

        parser = new FilteringStoryParser(filter("any('inherit', 'i2')"), true)
        story = parser.parseStory(STORY, null)
        // 007
        assert contains(story, "007")

        parser = new FilteringStoryParser(filter("any('inherit', 'i1', 'i2')"), true)
        story = parser.parseStory(STORY, null)
        assert contains(story, "001", "002", "003", "007")

        parser = new FilteringStoryParser(filter("any('inherit', '')"), true)
        story = parser.parseStory(STORY, null)
        // 008 matches
        assert contains(story, "008")
    }

    @Test
    void filterWithAll() {
        // Scenarios not having searched attribute are included by default, ignored and featured are not included
        def parser = new FilteringStoryParser(filter("all('attribute', 'a1', 'a2')"), true)
        def story = parser.parseStory(STORY, null)
        // All by default
        assert contains(story, "001", "003", "007", "008")

        parser = new FilteringStoryParser(filter("all('property', 'p1', 'p2')"), true)
        story = parser.parseStory(STORY, null)
        // 003 matches both
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter("all('property', 'p2', 'p3')"), true)
        story = parser.parseStory(STORY, null)
        // All by default
        assert contains(story, "001", "002", "007", "008")
    }

    @Test
    void filterWithOnly() {
        // Scenarios not having searched attribute are included by default, ignored and featured are not included
        def parser = new FilteringStoryParser(filter("only('property', 'p1', 'p2')"), true)
        def story = parser.parseStory(STORY, null)
        // 003 matches
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter("only('property','p1', 'p2', 'p3')"), true)
        story = parser.parseStory(STORY, null)
        // 003 matches
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter("only('property', 'p2', 'p3')"), true)
        story = parser.parseStory(STORY, null)
        // All by default
        assert contains(story, "001", "002", "007", "008")
    }

    @Test
    void filterWithNone() {
        // Scenarios not having searched attribute are included by default, ignored and featured are not included
        def parser = new FilteringStoryParser(filter("none('attribute', 'a2')"), true)
        def story = parser.parseStory(STORY, null)
        // 002 is excluded
        assert contains(story, "001", "003", "007", "008")

        parser = new FilteringStoryParser(filter("none('property', 'p2', 'p3')"), true)
        story = parser.parseStory(STORY, null)
        // 003 is excluded
        assert contains(story, "001", "002", "007", "008")

        parser = new FilteringStoryParser(filter("none('inherit', 'i1', 'i2')"), true)
        story = parser.parseStory(STORY, null)
        // All excluded except 008
        assert contains(story, "008")
    }

    @Test
    void filterWithEq() {
        // Scenarios not having searched attribute are included by default, ignored and featured are not included
        def parser = new FilteringStoryParser(filter("eq('inherit', 'i1')"), true)
        def story = parser.parseStory(STORY, null)
        // 001, 002, 003 have exact match
        assert contains(story, "001", "002", "003")
    }

    @Test
    void filterWithEqualsign() {
        def parser = new FilteringStoryParser(filter("inherit == 'i2'"), true)
        def story = parser.parseStory(STORY, null)
        //  Only 007 has inherit and it equals i2
        assert contains(story, "007")
    }

    @Test
    void filterWithExists() {
        def parser = new FilteringStoryParser(filter("exists('attribute')"), true)
        def story = parser.parseStory(STORY, null)
        // Stories with defined meta 'attribute'
        assert contains(story, "002")
    }

    @Test
    void filterWithMatches() {
        def parser = new FilteringStoryParser(filter("matches('id', '00[1|2|7]')"), true)
        def story = parser.parseStory(STORY, null)
        // Stories matching regexp
        assert contains(story, "001", "002", "007")
    }

    @Test
    void filterWithFeatures() {
        // All scenarios not having features meta are included by default, ignored are not included
        // List of features has to contain all required features for scenario
        def parser = new FilteringStoryParser(filter(null, "f1"), true)
        def story = parser.parseStory(STORY, null)
        // None scenarios require f1
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter(null, "f2"), true)
        story = parser.parseStory(STORY, null)
        // None scenarios require f2
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter(null, "f3"), true)
        story = parser.parseStory(STORY, null)
        // None scenarios require f3
        assert contains(story, "001", "002", "003", "007", "008")

        parser = new FilteringStoryParser(filter(null, "f1 f2"), true)
        story = parser.parseStory(STORY, null)
        // 004 requires f1 and f2
        assert contains(story, "001", "002", "003", "004", "007", "008")

        parser = new FilteringStoryParser(filter(null, "f2 f3"), true)
        story = parser.parseStory(STORY, null)
        // 005 requires f2 and f3
        assert contains(story, "001", "002", "003", "005", "007", "008")

        parser = new FilteringStoryParser(filter(null, "f1 f2 f3"), true)
        story = parser.parseStory(STORY, null)
        // 004, 005 requires f1, f2 and f3
        assert contains(story, "001", "002", "003", "004", "005", "007", "008")

        // No scenarios require f4
        parser = new FilteringStoryParser(filter(null, "f4"), true)
        story = parser.parseStory(STORY, null)
        assert contains(story, "001", "002", "003", "007", "008")
    }

    static MetaFilter filter(String filter = null, String features = null, boolean ignore = true) throws IOException {
        GroovyMetaFilterBuilder builder = new GroovyMetaFilterBuilder(java.lang.Object)
        if (StringUtils.isNotBlank(filter)) {
            builder.metaFilter(filter)
        }

        if (StringUtils.isNotBlank(features)) {
            builder.features(features)
        }

        builder.ignore(ignore)
        return builder.build()
    }

    static boolean contains(Story story, String... containsScenarioIds) {
        assert story != null
        assert story.getScenarios().size() == containsScenarioIds.length
        return story.scenarios.collect { i -> i.meta.getProperty("id") }.containsAll(containsScenarioIds)
    }
}
