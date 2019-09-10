package org.jbehavesupport.core.web;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event used by WebScreenshotReportExtension
 */
@Getter
public class WebScreenshotEvent extends ApplicationEvent {
    private WebScreenshotType type;

    /**
     * constructor
     * @param source class which published event
     * @param type   type of screenshot
     */
    public WebScreenshotEvent(Object source, WebScreenshotType type) {
        super(source);
        this.type = type;
    }
}
