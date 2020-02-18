package org.jbehavesupport.core.internal;

import java.lang.reflect.Type;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@UtilityClass
@Slf4j
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

    public static List<Triple<String, Object, String>> convertTriple(ExamplesTable table, String column1Header, String column2Header, String column3Header) {
        List<Triple<String, Object, String>> result = new ArrayList<>();
        List<Parameters> rows = table.getRowsAsParameters();
        rows.forEach(item -> {
            String column1 = item.valueAs(column1Header, String.class);
            Object column2 = null;
            Type dataType = getValueType(item, item.valueAs(column2Header, String.class));
            if (dataType != null) {
                column2 = item.valueAs(column2Header, dataType);
            }
            String column3 = item.values().containsKey(column3Header) ? item.valueAs(column3Header, String.class) : null;
            result.add(new ImmutableTriple<>(column1, column2, column3));
        });
        return result;
    }

    private static Type getValueType(Parameters item, String data) {
        if (item.values().containsKey(ExampleTableConstraints.TYPE)) {
            String typeName = item.valueAs(ExampleTableConstraints.TYPE, String.class);
            if (StringUtils.hasText(typeName)) {
                return chooseType(typeName, data);
            }
        }
        return String.class;
    }

    private static Type chooseType(String typeName, String data) {
        typeName = typeName.toLowerCase();
        switch (typeName) {
            case "boolean":
                return boolean.class;
            case "number":
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.getDefault());
                return data.contains(Character.toString(formatSymbols.getDecimalSeparator())) ? double.class : int.class;
            case "string":
                return String.class;
            default:
                throw new IllegalArgumentException("Provided class not found: " + typeName);
        }
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
     *
     * @param table       table to retrieve data from
     * @param keyColumn   header name to seek in for key
     * @param key         search key
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

    /**
     * Checks whether examplesTable has all mandatory columns
     *
     * @param examplesTable     table to be checked
     * @param expectedColumns   list of mandatory columns
     *
     * @throws org.assertj.core.api.SoftAssertionError   for every column missing in table
     * @throws java.lang.IllegalArgumentException        when expectedColumns is missing
     */
    public static void assertMandatoryColumns(ExamplesTable examplesTable, String ... expectedColumns) {
        Assert.notEmpty(expectedColumns, "expectedColumns must not be empty");
        SoftAssertions softly = new SoftAssertions();
        Arrays.stream(expectedColumns).forEach(key -> {
            if (examplesTable.getHeaders().stream().noneMatch(column -> column.equals(key))) {
                softly.fail("Examples table must contain column '" + key + "'");
            }
        });
        softly.assertAll();
    }

    /**
     * Checks whether examplesTable column contains any duplicity
     *
     * @param examplesTable     table to be checked
     * @param columns   list of mandatory columns
     *
     * @throws org.assertj.core.api.SoftAssertionError   for every column missing in table
     * @throws java.lang.IllegalArgumentException        when columns is missing
     */
    public static void assertDuplicatesInColumns(ExamplesTable examplesTable, String... columns) {
        Assert.notEmpty(columns, "columns must not be empty");
        SoftAssertions softly = new SoftAssertions();
        Arrays.stream(columns).forEach(column -> {
            if(!examplesTable.getHeaders().contains(column)){
                softly.fail("Examples table doesn't contain column: " + column);
            } else {
                LinkedHashSet<Integer> duplicates = new LinkedHashSet<>();
                for (int row = 0; row < examplesTable.getRowCount(); row++) {
                    if (duplicates.contains(row)) {
                        continue;
                    }
                    checkColumnForDuplicates(row, softly, duplicates, examplesTable, column);
                }
            }
        });
        softly.assertAll();
    }

    private static void checkColumnForDuplicates(int originalRowNum, SoftAssertions softly, LinkedHashSet<Integer> allDuplicates, ExamplesTable examplesTable, String column) {
        LinkedHashSet<Integer> duplicates = new LinkedHashSet<>();
        String originalRowValue = examplesTable.getRow(originalRowNum).get(column);
        for (int rowToCheckNum = originalRowNum + 1; rowToCheckNum < examplesTable.getRowCount(); rowToCheckNum++) {
            if (originalRowValue.equals(examplesTable.getRow(rowToCheckNum).get(column))) {
                duplicates.add(originalRowNum);
                duplicates.add(rowToCheckNum);
            }
        }
        if (!duplicates.isEmpty()) {
            allDuplicates.addAll(duplicates);
            softly.fail("Examples table contains duplicate value: [" + originalRowValue + "] in a column ["
                + column + "] in rows: " + duplicates.toString());
        }
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
