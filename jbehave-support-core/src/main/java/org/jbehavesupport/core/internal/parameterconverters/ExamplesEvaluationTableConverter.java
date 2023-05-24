package org.jbehavesupport.core.internal.parameterconverters;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.expression.ExpressionEvaluator;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamplesEvaluationTableConverter extends ParameterConverters.FromStringParameterConverter<ExamplesTable> {

    private final ExpressionEvaluator expressionEvaluator;

    private ExamplesTableFactory factory;

    public void setConfiguration(Configuration config) {
        this.factory = new ExamplesTableFactory(config);
    }

    @Override
    public boolean canConvertTo(Type type) {
        if (type instanceof Class<?>) {
            return ExamplesTable.class.isAssignableFrom((Class<?>) type);
        }
        return false;
    }

    @Override
    public ExamplesTable convertValue(final String value, final Type type) {
        ExamplesTable result = factory.createExamplesTable(value.trim());
        return result.withRows(evaluateRows(result.getRows()));
    }

    private List<Map<String, String>> evaluateRows(List<Map<String, String>> rows) {
        return rows.stream().map(this::translateRow).collect(Collectors.toList());
    }

    private Map<String, String> translateRow(Map<String, String> row) {
        return row.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    e -> expressionEvaluator.evaluate(e.getValue()).toString(),
                    (u, v) -> {
                        throw new IllegalStateException(String.format("Duplicate key %s", u));
                    },
                    LinkedHashMap::new
                )
            );
    }
}
