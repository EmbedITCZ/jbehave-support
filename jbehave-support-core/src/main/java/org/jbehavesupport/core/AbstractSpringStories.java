package org.jbehavesupport.core;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.RethrowingFailure;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.model.Meta;
import org.jbehave.core.parsers.StoryParser;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.FreemarkerViewGenerator;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterControls;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.jbehavesupport.core.internal.FullScenarioNameStorer;
import org.jbehavesupport.core.internal.SuffixRemovingStoryNameResolver;
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.internal.web.GivenStoryHelper;
import org.jbehavesupport.core.report.XmlReporterFactory;
import org.jbehavesupport.engine.EmbedderConfiguration;
import org.jbehavesupport.engine.JUnit5Stories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.test.context.TestContextManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.notNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractSpringStories extends JUnit5Stories {

    public static final String JBEHAVE_SCENARIO = "jbehave_scenario";

    private static final String CUSTOM_MATCHER = "custom";
    private static final Long STORY_TIMEOUTS = 600L;

    private static final String DOT_REGEXP = "\\.";
    private static final String SLASH = "/";
    private static final String STORY_FILE_EXTENSION = ".story";
    private static final String STORY_SUFFIX = "Story";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FullScenarioNameStorer fullScenarioNameStorer;

    @Autowired
    private GivenStoryHelper givenStoryHelper;

    @Autowired
    private ParameterConverters parameterConverters;

    @Autowired(required = false)
    private XmlReporterFactory xmlReporterFactory;

    private InjectableStepsFactory injectableStepsFactory;

    private Configuration configuration;

    // meta filters must be known in constructor
    private String groupsIncludeMetaFilter = System.getProperty("jbehave.groups.include");

    private final Long timeout;

    /**
     * Initialize story with step classes.
     * Using default timeout 600 seconds.
     */
    public AbstractSpringStories() {
        this(STORY_TIMEOUTS);
    }

    /**
     * Initialize story class with timeout.
     *
     * @param timeout timeout in seconds
     */
    public AbstractSpringStories(final Long timeout) {
        notNull(timeout, "Timeout must be defined");
        this.timeout = timeout;
        prepareTestInstance();
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        if (injectableStepsFactory == null) {
            injectableStepsFactory = new SpringStepsFactory(configuration, applicationContext);
        }
        return injectableStepsFactory;
    }

    /**
     * This will let implementor option to configure embedder if needed
     */
    @Override
    public Embedder configuredEmbedder() {
        Embedder embedder = super.configuredEmbedder();
        embedder.useMetaFilters(Collections.singletonList(CUSTOM_MATCHER));
        embedder.useMetaMatchers(metaMatchers());
        EmbedderConfiguration.recommendedConfiguration(embedder).useStoryTimeouts(timeout.toString());
        return embedder;
    }

    @Override
    public Configuration configuration() {
        if (configuration == null) {
            configuration = new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath())
                .useStoryReporterBuilder(storyReporterBuilder())
                .useParameterControls(new ParameterControls().useDelimiterNamedParameters(true))
                .usePendingStepStrategy(new FailingUponPendingStep())
                .useViewGenerator(new FreemarkerViewGenerator(new SuffixRemovingStoryNameResolver(), FreemarkerViewGenerator.class))
                .useParameterConverters(parameterConverters)
                .useFailureStrategy(new RethrowingFailure())
                .useStoryExecutionComparator((x, y) -> 1); // always return in the same order as passed

            StoryParser parser = storyParser(configuration);
            if (parser != null) {
                // Assign custom parser if it is defined, default is used if custom is not specified
                configuration.useStoryParser(parser);
            }

            // hacky code - unfortunately i haven't found another way how to properly setup factory and params converters
            applicationContext.getBean(ExamplesEvaluationTableConverter.class).setConfiguration(configuration);
        }
        return configuration;
    }

    /**
     * If children require different parser they may override this method
     */
    protected StoryParser storyParser(Configuration configuration) {
        return null;
    }


    @Override
    protected List<String> storyPaths() {
        Class<? extends AbstractSpringStories> clazz = getClass();
        return buildStoryPath(
            clazz.getPackage().getName().replaceAll(DOT_REGEXP, SLASH),
            StringUtils.removeEnd(FilenameUtils.removeExtension(clazz.getSimpleName()), STORY_SUFFIX)
        );
    }

    private List<String> buildStoryPath(final String path, final String storyName) {
        try {
            return loadResources(path, storyName)
                .filter(Resource::isReadable)
                .map(r -> getPath(path, r))
                .toList();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getPath(String path, Resource resource) {
        if (resource instanceof ClassPathResource classPathResource) {
            return classPathResource.getPath();
        } else {
            try {
                String fullPathWithSlashes = resource.getFile().getPath().replace("\\", "/");
                return fullPathWithSlashes.substring(fullPathWithSlashes.indexOf(path));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Load specific resources based on path (must be on classpath) and story name.
     * e.g. <code>
     * Stream<Resource> resources = loadResources("org/jbehavesupport/test", "TUS7009");
     * </code>
     *
     * @param path      path on classpath
     * @param storyName
     * @return resources from classpath
     * @throws IOException
     */
    protected Stream<Resource> loadResources(String path, String storyName) throws IOException {
        return Stream.of(
            new PathMatchingResourcePatternResolver(getClass().getClassLoader())
                .getResources(String.format("classpath:%s/%s*%s", path, storyName, STORY_FILE_EXTENSION))
        );
    }

    private void prepareTestInstance() {
        TestContextManager testContextManager = new TestContextManager(getClass());
        try {
            testContextManager.prepareTestInstance(this);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, MetaFilter.MetaMatcher> metaMatchers() {
        HashMap<String, MetaFilter.MetaMatcher> map = new HashMap<>();
        map.put(CUSTOM_MATCHER, new CustomMetaMatcher());
        return map;
    }

    protected StoryReporterBuilder storyReporterBuilder() {
        StoryReporterBuilder storyReporterBuilder = new StoryReporterBuilder()
            .withCodeLocation(CodeLocations.codeLocationFromClass(getClass()))
            .withPathResolver(new FilePrintStreamFactory.ResolveToPackagedName())
            .withFailureTrace(true)
            .withDefaultFormats()
            .withReporters(fullScenarioNameStorer, givenStoryHelper);
        if (nonNull(xmlReporterFactory)) {
            storyReporterBuilder.withFormats(xmlReporterFactory);
        }
        return storyReporterBuilder;
    }

    private class CustomMetaMatcher implements MetaFilter.MetaMatcher {

        @Override
        public void parse(final String filterAsString) {
            //nop No need to parsing here
        }

        @Override
        public boolean match(final Meta meta) {
            if (meta.hasProperty("skip")) {
                return false;
            }

            if (groupsIncludeMetaFilter == null || groupsIncludeMetaFilter.isEmpty()) {
                return true;
            } else if (!meta.getProperty("group").isEmpty()) {
                String[] groups = groupsIncludeMetaFilter.trim().split(",");
                for (int i = groups.length - 1; i >= 0; i--) {
                    if (meta.getProperty("group").matches("^.*\\b" + groups[i] + "\\b.*$")) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
