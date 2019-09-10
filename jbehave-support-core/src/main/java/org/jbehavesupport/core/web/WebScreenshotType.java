package org.jbehavesupport.core.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Modes for ScreenshotReportExtension
 */
@AllArgsConstructor
@Getter
public enum WebScreenshotType {
    FAILED(0),
    MANUAL(0),
    WAIT(1),
    STEP(2),
    DEBUG(3);

    private final int hierarchy;
}

