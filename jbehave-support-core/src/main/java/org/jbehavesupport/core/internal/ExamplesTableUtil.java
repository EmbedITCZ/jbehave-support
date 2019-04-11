package org.jbehavesupport.core.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.springframework.util.Assert;

@UtilityClass
public class ExamplesTableUtil {

    /**
     * Transforms {@link ExamplesTable} values to {@link Map} entries.
     *
     * @param table the jbehave examples table
     * @param key   header name to be used as map key
     * @param value header name to be used as map value
     * @return the map
     */
    public static Map<String, String> convertMap(@NonNull ExamplesTable table, String key, String value) {
        Map<String, String> result = new HashMap<>();
        List<Parameters> rows = table.getRowsAsParameters();
        rows.forEach(item -> {
            String name = item.valueAs(key, String.class);
            String data = item.valueAs(value, String.class);
            result.put(name, data);
        });
        return result;
    }

    public static List<Triple<String, String, String>> convertTriple(ExamplesTable table, String column1Header, String column2Header, String column3Header) {
        List<Triple<String, String, String>> result = new ArrayList<>();
        List<Parameters> rows = table.getRowsAsParameters();
        rows.forEach(item -> {
            String column1 = item.valueAs(column1Header, String.class);
            String column2 = item.valueAs(column2Header, String.class);
            String column3 = item.values().containsKey(column3Header) ? item.valueAs(column3Header, String.class) : null;
            result.add(new ImmutableTriple<>(column1, column2, column3));
        });
        return result;
    }

    /**
     * Create {@link List} which contains {@link Map} with converted values for every row of {@link ExamplesTable}.
     *
     * @param table input table
     * @return input table converted to a list of maps
     */
    public static List<Map<String, String>> convertTable(@NonNull ExamplesTable table) {
        return convertTable(table, true);
    }

    public static List<Map<String, String>> convertTableWithUpperCaseKeys(@NonNull ExamplesTable table) {
        return convertTable(table, false);
    }

    /**
     * Checks whether provided table has some value under certain column header
     *
     * @param table     table to be checked
     * @param column    column to be searched in, or null if unrestricted
     * @param predicate predicate to be used in value comparison
     * @return true if table contains searched value, false otherwise
     */
    public boolean tableContains(@NonNull ExamplesTable table, Object column, Predicate<? super String> predicate) {
        if (column == null) {
            return table.getRows().stream()
                .flatMap(me -> me.values().stream())
                .anyMatch(predicate);
        } else {
            return table.getRows().stream()
                .map(me -> me.get(column))
                .anyMatch(predicate);
        }
    }

    /**
     * Retrieves value from specified column based on key present in different column.
     * @param table table to retrieve data from
     * @param keyColumn header name to seek in for key
     * @param key search key
     * @param valueColumn column to get the value from
     * @return
     */
    public static String getValue(ExamplesTable table, String keyColumn, String key, String valueColumn) {
        Assert.notNull(table, "Examples table must be provided");
        Assert.notEmpty(table.getHeaders(), "Examples table has no headers");
        Assert.isTrue(table.getHeaders().contains(keyColumn), "Column " + keyColumn + " is not present in examples table");
        Assert.isTrue(table.getHeaders().contains(valueColumn), "Column " + valueColumn + " is not present in examples table");

        return table.getRows().stream()
            .filter(row -> row.get(keyColumn).equals(key))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Record not found in examples table under key: " + keyColumn))
            .get(valueColumn);
    }

    private static List<Map<String, String>> convertTable(ExamplesTable table, boolean caseSensitiveMap) {
        List<String> headers = table.getHeaders();
        return table.getRowsAsParameters()
            .stream()
            .map(p -> {
                Map<String, String> row = getHashMap(caseSensitiveMap);
                headers.forEach(header -> row.put(header, p.valueAs(header, String.class)));
                return row;
            })
            .collect(Collectors.toList());
    }

    private static Map<String, String> getHashMap(final boolean caseSensitive) {
        Map<String, String> result;
        if (caseSensitive) {
            result = new HashMap<>();
        } else {
            result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        return result;
    }

}
