package org.jbehavesupport.core.web;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Class holds data related to {@link WebProperty}.
 */
@Value
@Builder
@RequiredArgsConstructor
public class WebPropertyContext {

    private String page;
    private String element;

}
