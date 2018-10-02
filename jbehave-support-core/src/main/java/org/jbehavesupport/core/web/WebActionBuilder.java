package org.jbehavesupport.core.web;

import org.jbehave.core.model.ExamplesTable;

/**
 * Builder for {@link ExamplesTable} containing {@link WebAction}.
 * <p>
 * Example:
 * <pre>
 *  ExampleTable loginActions = webActionBuilder.builder()
 *         .on("#username").fill("admin")
 *         .on("#password").fill("123456")
 *         .on("#submit").click()
 *         .buildExamplesTable();
 * </pre>
 * will produce:
 * <pre>
 * | element   | action | value  |
 * | #username | FILL   | admin  |
 * | #password | FILL   | 123456 |
 * | #submit   | CLICK  |        |
 * </pre>
 */
public interface WebActionBuilder {

    Builder builder();

    interface Builder {

        ActionStep on(String elementName);

        Builder acceptAlert();

        Builder dismissAlert();

        Builder headerSeparator(String headerSeparator);

        Builder valueSeparator(String valueSeparator);

        ExamplesTable buildExamplesTable();

    }

    interface ActionStep {

        ValueStep perform(String actionName);

        Builder clear();

        Builder click();

        Builder doubleClick();

        Builder fill(String value);

        Builder press(String value);

        Builder select(String value);

    }

    interface ValueStep extends TerminalStep {

        AliasStep value(String value);

    }

    interface AliasStep extends TerminalStep {

        TerminalStep alias(String alias);

    }

    interface TerminalStep {

        Builder and();

    }

}
