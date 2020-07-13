package org.jbehavesupport.core.internal.splunk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SplunkArgNames {
    EARLIEST_TIME("earliest_time"),
    LATEST_TIME("latest_time"),
    OUTPUT_MODE("output_mode");

    private String argName;
}
