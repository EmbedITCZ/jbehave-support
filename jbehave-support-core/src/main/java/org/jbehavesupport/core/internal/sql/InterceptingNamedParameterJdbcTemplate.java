package org.jbehavesupport.core.internal.sql;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jbehavesupport.core.report.extension.SqlXmlReporterExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class InterceptingNamedParameterJdbcTemplate {

    private SqlXmlReporterExtension reporterExtension;
    private NamedParameterJdbcTemplate jdbcTemplate;

    public InterceptingNamedParameterJdbcTemplate(DataSource dataSource, SqlXmlReporterExtension reporterExtension) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.reporterExtension = reporterExtension;
    }

    public int update(String sql, Map<String, ?> paramMap) {
        if (reporterExtension != null) {
            reporterExtension.addSqlStatement(SqlStatementContext.builder()
                .statement(sql)
                .paramMap(paramMap)
                .build());
        }
        return jdbcTemplate.update(sql, paramMap);
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) {
        SqlStatementContext sqlStatementContext = null;
        if (reporterExtension != null) {
            sqlStatementContext = SqlStatementContext.builder()
                .statement(sql)
                .paramMap(paramMap)
                .build();
            reporterExtension.addSqlStatement(sqlStatementContext);
        }

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, paramMap);
        if (sqlStatementContext != null) {
            sqlStatementContext.setResults(resultList);
        }
        return resultList;
    }

}
