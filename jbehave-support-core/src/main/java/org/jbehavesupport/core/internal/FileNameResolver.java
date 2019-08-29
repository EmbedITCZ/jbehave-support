package org.jbehavesupport.core.internal;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.jbehavesupport.core.AbstractSpringStories;
import org.jbehavesupport.core.TestContext;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class FileNameResolver {

    private final TestContext testContext;

    public Path resolveFilePath(String fileNamePattern, String directory, String... patternArguments) {
        if (testContext.contains(AbstractSpringStories.JBEHAVE_SCENARIO)) {
            final String storyName = testContext.get(AbstractSpringStories.JBEHAVE_SCENARIO, String.class).split("#")[0];
            Path destinationPath = Paths.get(directory, String.format(fileNamePattern, ArrayUtils.add(patternArguments, storyName)));
            int i = 1;
            while (destinationPath.toFile().exists()) {
                destinationPath = Paths.get(directory, String.format(fileNamePattern, ArrayUtils.add(patternArguments, storyName + "-" + i++)));
            }
            return destinationPath;
        } else {
            return Paths.get(directory, String.format(fileNamePattern, ArrayUtils.add(patternArguments, String.valueOf(System.currentTimeMillis()))));
        }
    }
}
