package org.jbehavesupport.core.internal.web.action;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.jbehavesupport.core.web.WebAction;
import org.jbehavesupport.core.web.WebActionResolver;

public class WebActionResolverImpl implements WebActionResolver {

    private final Map<String, WebAction> actions;

    public WebActionResolverImpl(List<WebAction> actions) {
        this.actions = actions.stream()
            .collect(toMap(WebAction::name, e -> e));
    }

    @Override
    public WebAction resolveAction(String actionName) {
        WebAction action = actions.get(actionName);
        if (action == null) {
            throw new IllegalArgumentException("Unable to resolve action with name " + actionName);
        }
        return action;
    }

}
