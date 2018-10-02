package org.jbehavesupport.core.internal.expression;

import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionCommand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BytesCommand implements ExpressionCommand {

    private final TestContext ctx;

    @Override
    public Object execute(Object... params) {
        isTrue(params.length == 1, "Exactly 1 parameter is required");
        notNull(params[0], "First parameter must not be null");
        isInstanceOf(String.class, params[0], "Parameter must be string");

        String referenceKey = ctx.createReferenceKey();
        ctx.put(referenceKey, ((String)params[0]).getBytes());
        return referenceKey;
    }
}
