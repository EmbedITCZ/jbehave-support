package org.jbehavesupport.core.internal;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ExampleTableConstraints {

    public static final String NAME = "name";
    public static final String DATA = "data";
    public static final String ALIAS = "contextAlias";
    public static final String EXPECTED_VALUE = "expectedValue";
    public static final String VERIFIER = "verifier";
    public static final String TYPE = "type";
    /**
     * @deprecated(since = "1.0.0", forRemoval = true) use {@link #VERIFIER} instead
     */
    @Deprecated
    public static final String OPERATOR = "operator";

}
