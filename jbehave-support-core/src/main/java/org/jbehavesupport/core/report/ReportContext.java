package org.jbehavesupport.core.report;

import static java.util.Objects.nonNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jbehave.core.model.Narrative;

@Getter
@Setter
@Accessors(fluent = true)
public class ReportContext {

    public enum Status {
        SUCCESSFUL, FAILED
    }

    private Long startExecution;
    private Long endExecution;
    private Status status = Status.SUCCESSFUL;
    private Map<String, String> metaInfo = new HashMap<>();
    private Narrative narrative;

    public Long duration() {
        Long result;
        if (nonNull(startExecution) && nonNull(endExecution)) {
            result = endExecution - startExecution;
        } else {
            result = null;
        }
        return result;
    }

    public void statusFail() {
        this.status = Status.FAILED;
    }

    public ZonedDateTime startExecutionZoned() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(startExecution), ZoneId.systemDefault());
    }

    public ZonedDateTime endExecutionZoned() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(endExecution), ZoneId.systemDefault());
    }
}
