package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionCommand;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * provides serialized byte[] from provided resource.<br/>
 * param[0]: resource in package
 */
@Component
@RequiredArgsConstructor
public class ResourceCommand implements ExpressionCommand {

    private final ResourceLoader resourceLoader;
    private final TestContext ctx;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1, "Exactly 1 parameter is required");
        notNull(params[0], "First parameter must not be null");
        isInstanceOf(String.class, params[0], "Parameter must be string");

        String referenceKey = ctx.createReferenceKey();
        ctx.put(referenceKey, resourceLoader.getResource((String)params[0]));
        return referenceKey;
    }
}
