package org.jbehavesupport.core.internal;

import lombok.experimental.UtilityClass;
import org.jbehavesupport.core.TestContext;

@UtilityClass
public class MetadataUtil {

    private static final String TYPE = "TYPE";
    private static final TestContext.Metadata USER_DEFINED = TestContext.Metadata.of("USER_DEFINED", "USER_DEFINED");

    public static TestContext.Metadata userDefined() {
        return USER_DEFINED;
    }

    public static TestContext.Metadata type(String type) {
        return TestContext.Metadata.of(TYPE, getClass(type));
    }

    private static Class<?> getClass(final String type) {
        try {
            return Class.forName(type);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Provided class not found", e);
        }
    }

    public static boolean isType(TestContext.Metadata metadata) {
        return TYPE.equals(metadata.getName());
    }

}
