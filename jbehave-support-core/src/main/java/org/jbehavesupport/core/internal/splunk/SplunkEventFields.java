package org.jbehavesupport.core.internal.splunk;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
@Getter
public class SplunkEventFields {
    public final String TIME = "_time";
    public final String LEVEL = "level";
    public final String MESSAGE = "message";
    public final String NAMESPACE = "namespace";
    public final String INDEX = "index";
    public final String HOST = "host";
}
