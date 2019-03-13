package org.jbehavesupport.core.report.extension;

import org.jbehavesupport.core.internal.sql.SqlStatementContext;
import org.jbehavesupport.core.report.ReportContext;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

public class SqlXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String SQL_XML_REPORTER_EXTENSION = "sql";
    private static final String STATEMENT_RESULT_TAG = "statementResult";
    private static final String STATEMENT_TAG = "statement";
    private static final String PARAMETERS_TAG = "parameters";
    private static final String PARAMETER_TAG = "parameter";
    private static final String NAME_TAG = "name";
    private static final String VALUE_TAG = "value";
    private static final String RESULTS_TAG = "results";
    private static final String COLUMNS_TAG = "columns";
    private static final String COLUMN_TAG = "column";
    private static final String ROWS_TAG = "rows";
    private static final String ROW_TAG = "row";

    private final List<SqlStatementContext> queries = new ArrayList<>();

    @Override
    public String getName() {
        return SQL_XML_REPORTER_EXTENSION;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        queries.forEach(sql -> printSqlQuery(writer, sql));
        queries.clear();
    }

    public void addSqlStatement(SqlStatementContext sqlStatementContext) {
        queries.add(sqlStatementContext);
    }


    private void printSqlQuery(final Writer writer, final SqlStatementContext sqlStatementContext) {
        printBegin(writer, STATEMENT_RESULT_TAG);

        printBegin(writer, STATEMENT_TAG);
        printCData(writer, sqlStatementContext.getStatement());
        printEnd(writer, STATEMENT_TAG);

        if(!isEmpty(sqlStatementContext.getParamMap())) {
            printBegin(writer, PARAMETERS_TAG);
            sqlStatementContext.getParamMap().entrySet().stream()
                .forEach(param -> printParameter(writer, param));
            printEnd(writer, PARAMETERS_TAG);
        }

        if (!isEmpty(sqlStatementContext.getResults())) {
            printBegin(writer, RESULTS_TAG);
            printResults(writer, sqlStatementContext.getResults());
            printEnd(writer, RESULTS_TAG);
        }

        printEnd(writer, STATEMENT_RESULT_TAG);
    }

    private void printResults(Writer writer, List<Map<String, Object>> results) {
        printColumns(writer, results);

        printBegin(writer, ROWS_TAG);
        results.stream()
            .forEachOrdered(row -> printRow(writer, row));
        printEnd(writer, ROWS_TAG);
    }

    private void printRow(Writer writer, Map<String, Object> row) {
        printBegin(writer, ROW_TAG);
        row.values().stream()
            .forEachOrdered(value -> printRowValue(writer, value));
        printEnd(writer, ROW_TAG);
    }

    private void printRowValue(Writer writer, Object value) {
        if (value == null) {
            printSelfClosed(writer, VALUE_TAG, null);
        } else {
            printBegin(writer, VALUE_TAG);
            printCData(writer, value.toString());
            printEnd(writer, VALUE_TAG);
        }
    }

    private void printColumns(Writer writer, List<Map<String, Object>> results) {
        printBegin(writer, COLUMNS_TAG);

        results.stream()
            .findFirst()
            .ifPresent(row -> row.keySet().stream()
                .forEachOrdered(column -> {
                    printBegin(writer, COLUMN_TAG);
                    printCData(writer, column);
                    printEnd(writer, COLUMN_TAG);
                })
            );

        printEnd(writer, COLUMNS_TAG);
    }

    private void printParameter(Writer writer, Map.Entry<String,?> param) {
        printBegin(writer, PARAMETER_TAG);

        printBegin(writer, NAME_TAG);
        printString(writer, param.getKey());
        printEnd(writer, NAME_TAG);

        printBegin(writer, VALUE_TAG);
        printCData(writer, param.getValue().toString());
        printEnd(writer, VALUE_TAG);

        printEnd(writer, PARAMETER_TAG);
    }

}
