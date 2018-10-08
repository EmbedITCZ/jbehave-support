package org.jbehavesupport.core.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.parsers.RegexStoryParser;


/**
 * Story parser assigning given name and filtering stories with given filter
 */
@Slf4j
public class FilteringStoryParser extends RegexStoryParser {
    public static final String META_BY_ROW = "metaByRow";

    private final MetaFilter filter;
    private final boolean metaByRow;
    private final String storyAsMetaPrefix;
    private final String scenarioAsMetaPrefix;

    private final Function<String, String> nameGenerator;

    /**
     * Constructor for creating parser with default meta prefixes
     */
    public FilteringStoryParser(MetaFilter filter, boolean metaByRow) {
        this(filter, metaByRow, null, "", "");
    }

    public FilteringStoryParser(MetaFilter filter, boolean metaByRow, Function<String, String> nameGenerator) {
        this(filter, metaByRow, nameGenerator, "", "");
    }

    /**
     * Full constructor for creating parser and defining story and scenario asMeta() prefixes allowing to use scenario name or description as metadata
     * and distinguish them from true metadata attributes.
     * <p>
     * You can also specify story nameGenerator - this is function accepting storyPath as a parameter and generating custom story name.
     * Story name has to be unique during test execution session
     */
    public FilteringStoryParser(MetaFilter filter, boolean metaByRow, Function<String, String> nameGenerator, String storyAsMetaPrefix, String scenarioAsMetaPrefix) {
        this.filter = filter;
        this.metaByRow = metaByRow;
        this.storyAsMetaPrefix = storyAsMetaPrefix;
        this.scenarioAsMetaPrefix = scenarioAsMetaPrefix;
        this.nameGenerator = nameGenerator;
    }

    /**
     * Parses and returns stories containing only relevant scenarios
     */
    @Override
    public Story parseStory(String storyAsText) {
        return this.parseStory(storyAsText, null);
    }

    /**
     * Parses and returns stories containing only relevant scenarios
     */
    @Override
    public Story parseStory(String storyAsText, String storyPath) {
        return filter(super.parseStory(storyAsText, storyPath));
    }

    protected Story filter(Story story) {
        List<Scenario> scenarios = new ArrayList<>();
        Meta storyMeta = story.getMeta().inheritFrom(story.asMeta(storyAsMetaPrefix));
        scenarios.addAll(story.getScenarios().stream()
            // Filtering relevant stories
            .filter(scenario -> filter == null
                || scenario.getExamplesTable().getRowCount() > 0 && this.isMetaByRow(scenario)
                || filter.allow(scenario.getMeta().inheritFrom(scenario.asMeta(scenarioAsMetaPrefix)
                .inheritFrom(storyMeta))))
            .collect(Collectors.toList()));

        Story result = new Story(story.getPath(),
            story.getDescription(),
            story.getMeta(),
            story.getNarrative(),
            story.getGivenStories(),
            story.getLifecycle(),
            scenarios
        );

        result.namedAs(nameGenerator != null ? nameGenerator.apply(story.getPath()) : story.getName());
        return result;
    }


    protected boolean isMetaByRow(Scenario scenario) {
        return scenario.getExamplesTable().getProperties().containsKey(META_BY_ROW) ? scenario.getExamplesTable().metaByRow() : metaByRow;
    }
}
