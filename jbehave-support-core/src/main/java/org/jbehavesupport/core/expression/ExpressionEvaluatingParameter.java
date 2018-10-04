package org.jbehavesupport.core.expression;

import lombok.Value;

/**
 * The type of step parameter, which value can be some expression,
 * this expression will be evaluated by {@link ExpressionEvaluator}.
 * <p>
 * Example step:
 * <pre>
 * &#064;Given("the value [$value] is saved as [$contextAlias]")
 * public void saveValue(ExpressionEvaluatingParameter&lt;String&gt; value, String contextAlias) {
 *    ...
 * }
 * </pre>
 * <p>
 * Example usage:
 * <pre>
 * Given the value [{CURRENT_DATE}] is saved as [MY_DATE]
 * </pre>
 */
@Value
public class ExpressionEvaluatingParameter<T> {

    private T value;

}
