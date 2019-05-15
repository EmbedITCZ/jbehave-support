package org.jbehavesupport.core.internal.web.action;

import static java.lang.System.lineSeparator;

import java.util.ArrayList;
import java.util.List;

import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter;
import org.jbehavesupport.core.web.WebActionBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jbehave.core.model.ExamplesTable;

@RequiredArgsConstructor
public class WebActionBuilderImpl implements WebActionBuilder {

    private final ExamplesEvaluationTableConverter examplesTableConverter;

    @Override
    public Builder builder() {
        return new BuilderImpl(examplesTableConverter);
    }

    @RequiredArgsConstructor
    private static class BuilderImpl implements Builder {

        private static final String DEFAULT_SEPARATOR = "|";

        private final ExamplesEvaluationTableConverter examplesTableConverter;
        private final List<Row> rows = new ArrayList<>();
        private String headerSeparator = DEFAULT_SEPARATOR;
        private String valueSeparator = DEFAULT_SEPARATOR;

        @Override
        public ActionStep on(String elementName) {
            Row row = new Row(this, elementName);
            rows.add(row);
            return row;
        }

        @Override
        public Builder headerSeparator(String headerSeparator) {
            this.headerSeparator = headerSeparator;
            return this;
        }

        @Override
        public Builder valueSeparator(String valueSeparator) {
            this.valueSeparator = valueSeparator;
            return this;
        }

        @Override
        public Builder acceptAlert() {
            return on("@alert").perform("ACCEPT").and();
        }

        @Override
        public Builder dismissAlert() {
            return on("@alert").perform("DISMISS").and();
        }

        @Override
        public ExamplesTable buildExamplesTable() {
            StringBuilder sb = new StringBuilder();
            appendInlinedProperties(sb);
            appendHeader(sb);
            appendRows(sb);
            return (ExamplesTable) examplesTableConverter.convertValue(sb.toString(), ExamplesTable.class);
        }

        private void appendInlinedProperties(StringBuilder sb) {
            if (!DEFAULT_SEPARATOR.equals(headerSeparator) || !DEFAULT_SEPARATOR.equals(valueSeparator)) {
                sb.append("{headerSeparator=")
                    .append(headerSeparator)
                    .append(",valueSeparator=")
                    .append(valueSeparator)
                    .append("}");
            }
        }

        private void appendHeader(StringBuilder sb) {
            sb.append(headerSeparator)
                .append("element")
                .append(headerSeparator)
                .append("action")
                .append(headerSeparator)
                .append("value")
                .append(headerSeparator)
                .append("alias")
                .append(headerSeparator)
                .append(lineSeparator());
        }

        private void appendRows(StringBuilder sb) {
            rows.forEach(row ->
                sb.append(valueSeparator)
                    .append(row.getElementName())
                    .append(valueSeparator)
                    .append(row.getActionName())
                    .append(valueSeparator)
                    .append(row.getValue())
                    .append(valueSeparator)
                    .append(row.getAlias())
                    .append(valueSeparator)
                    .append(lineSeparator()));
        }

    }

    @Getter
    @RequiredArgsConstructor
    private static class Row implements ActionStep, ValueStep, AliasStep, TerminalStep {

        private final Builder builder;
        private final String elementName;
        private String actionName = "";
        private String value = "";
        private String alias = "";

        @Override
        public ValueStep perform(String actionName) {
            this.actionName = actionName;
            return this;
        }

        @Override
        public Builder clear() {
            return perform("CLEAR").and();
        }

        @Override
        public Builder click() {
            return perform("CLICK").and();
        }

        @Override
        public Builder doubleClick() {
            return perform("DOUBLE_CLICK").and();
        }

        @Override
        public Builder fill(String value) {
            return perform("FILL").value(value).and();
        }

        @Override
        public Builder press(String value) {
            return perform("PRESS").value(value).and();
        }

        @Override
        public Builder select(String value) {
            return perform("SELECT").value(value).and();
        }

        @Override
        public AliasStep value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public TerminalStep alias(String alias) {
            this.alias = alias;
            return this;
        }

        @Override
        public Builder and() {
            return builder;
        }

    }

}
