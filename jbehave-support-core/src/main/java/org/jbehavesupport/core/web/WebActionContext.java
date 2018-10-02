package org.jbehavesupport.core.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Class holds data related to {@link WebAction}.
 */
@Value
@Builder
@RequiredArgsConstructor
public class WebActionContext {

    private String page;
    private String element;
    private String data;
    private String alias;

}
