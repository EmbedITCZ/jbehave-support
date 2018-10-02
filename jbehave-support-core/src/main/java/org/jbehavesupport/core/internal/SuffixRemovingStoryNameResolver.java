package org.jbehavesupport.core.internal;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.springframework.util.Assert.notNull;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.io.StoryNameResolver;

public class SuffixRemovingStoryNameResolver implements StoryNameResolver {

    public static final String SUFFIX = ".story";

    @Override
    public String resolveName(String path) {
        notNull(path, "Provided path must not be null");
        String name = path;
        if (name.endsWith(SUFFIX)) {
            name = name.replace(SUFFIX, "");
        }
        if (name.contains("/")) {
            name = substringAfterLast(name, "/");
        }
        if (name.contains(".")) {
            name = substringAfterLast(name, ".");
        }
        name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), ' ');
        return name;
    }
}
