package org.jbehavesupport.core.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Class holds data related to {@link WebWaitCondition}.
 */
@Value
@Builder
@RequiredArgsConstructor
public class WebWaitConditionContext {

    private String page;
    private String element;
    private String condition;
    private String value;

}
