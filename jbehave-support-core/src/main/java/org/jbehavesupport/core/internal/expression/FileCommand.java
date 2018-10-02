package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.jbehavesupport.core.expression.ExpressionCommand;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Provides canonical path to file, using {@link ResourceLoader}.
 * param 1 - the resource location, e.g. org/jbehavesupport/core/expression/FileCommandTest.class
 * param 2 - optional name of file
 */
@Component
@RequiredArgsConstructor
public class FileCommand implements ExpressionCommand {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private final ResourceLoader resourceLoader;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length >= 1, "At least one parameter required");
        isTrue(params.length <= 2, "Maximum of 2 parameters allowed");
        isInstanceOf(String.class, params[0], "First parameter must be string");

        Resource resource = resourceLoader.getResource((String) params[0]);
        if (!resource.exists()) {
            throw new IllegalStateException("Provided resource location does not exist");
        }
        try {
            String destFileName = params.length > 1 ? params[1].toString() : resource.getFilename();
            File destFile = new File(TEMP_DIR, destFileName);
            destFile.deleteOnExit();
            if (!destFile.exists()) {
                FileUtils.copyInputStreamToFile(resource.getInputStream(), destFile);
            }

            return destFile.getCanonicalPath();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
