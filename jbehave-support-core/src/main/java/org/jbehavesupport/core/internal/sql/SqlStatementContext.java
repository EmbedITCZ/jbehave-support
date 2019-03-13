package org.jbehavesupport.core.internal.sql;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SqlStatementContext {

    private String statement;
    private Map<String, ?> paramMap;
    private List<Map<String, Object>> results;

}
