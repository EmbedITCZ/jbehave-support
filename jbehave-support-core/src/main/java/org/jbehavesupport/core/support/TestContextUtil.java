package org.jbehavesupport.core.support;

import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

import java.util.List;
import java.util.Map;

import org.jbehavesupport.core.TestContext;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Strings;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.ExamplesTableUtil;
import org.jbehavesupport.core.internal.MetadataUtil;

/**
 * Context utility.
 */
@UtilityClass
public class TestContextUtil {
    private static final String CONTEXT_DELIMITER = ".";
    private static final String COLUMN_TYPE = "type";

    /**
     * Put all data from example table to test context.
     * As a keys records from the "name" column are used with prefix.
     *
     * @param testContext current test context
     * @param data        example table with two columns "name" and "data"
     * @param prefix
     */
    public static void putDataIntoContext(final TestContext testContext, final ExamplesTable data, final String prefix) {
        List<Parameters> rows = data.getRowsAsParameters();
        rows.forEach(row -> {
            String name = row.valueAs(ExampleTableConstraints.NAME, String.class);
            String key = (!Strings.isNullOrEmpty(prefix)) ? String.join(CONTEXT_DELIMITER, prefix, name) : name;
            String value = row.valueAs(ExampleTableConstraints.DATA, String.class);
            String type = row.values().containsKey(COLUMN_TYPE) ? row.valueAs(COLUMN_TYPE, String.class) : null;

            if (StringUtils.isNoneEmpty(type)) {
                testContext.put(key, null, MetadataUtil.type(type));
            } else {
                testContext.put(key, value);
            }

            if (data.getHeaders().contains(ExampleTableConstraints.ALIAS)) {
                String alias = row.valueAs(ExampleTableConstraints.ALIAS, String.class);
                if (!alias.isEmpty()) {
                    testContext.put(alias, value, MetadataUtil.userDefined());
                }
            }
        });
    }

    /**
     * Put all data from example table to test context.
     * As a keys records from the "name" column are used.
     *
     * @param testContext current test context
     * @param data        example table with two columns "name" and "data"
     */
    public static void putDataIntoContext(final TestContext testContext, final ExamplesTable data) {
        putDataIntoContext(testContext, data, null);
    }

    /**
     * Put all data from example table to test context.
     *
     * @param testContext current test context
     * @param data        example table with data
     * @param key         column name which is used as key in test context
     * @param value       column name which is used as value in test context
     */
    public static void putDataIntoContext(final TestContext testContext, final ExamplesTable data, final String key, final String value) {
        if (data.getHeaders().contains(ExampleTableConstraints.ALIAS)) {
            Map<String, String> contextAliasMapping = ExamplesTableUtil.convertMap(data, key, value);
            for (String aliasKey : contextAliasMapping.keySet()) {
                if (isNoneEmpty(aliasKey)) {
                    testContext.put(aliasKey, contextAliasMapping.get(aliasKey), MetadataUtil.userDefined());
                }
            }
        }
    }

    public static Object escape(Object value) {
        if (value instanceof String) {
            return ((String) value).replace(":", "\\:").replace("{", "\\{").replace("}", "\\}");
        }

        return value;
    }

    public static Object unescape(Object value) {
        if (value instanceof String) {
            return ((String) value).replace("\\:", ":").replace("\\{", "{").replace("\\}", "}");
        }

        return value;
    }
}
