package org.jbehavesupport.core.internal.web.waitcondition;

import java.util.List;

import org.jbehavesupport.core.web.WebWaitCondition;
import org.jbehavesupport.core.web.WebWaitConditionContext;
import org.jbehavesupport.core.web.WebWaitConditionResolver;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WebWaitConditionResolverImpl implements WebWaitConditionResolver {

    private final List<WebWaitCondition> waitConditions;

    @Override
    public WebWaitCondition resolveWaitCondition(WebWaitConditionContext ctx) {
        return waitConditions.stream()
            .filter(c -> c.match(ctx))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Given condition [" + ctx.getCondition() + "] does not match."));
    }

}
