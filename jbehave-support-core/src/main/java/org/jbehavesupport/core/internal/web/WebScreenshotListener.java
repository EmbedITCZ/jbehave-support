package org.jbehavesupport.core.internal.web;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.web.WebScreenshotEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import org.jbehavesupport.core.web.WebScreenshotType;

@RequiredArgsConstructor
@Component
public class WebScreenshotListener implements ApplicationListener<WebScreenshotEvent> {
    private final WebScreenshotCreator screenshotCreator;
    @Value("${web.screenshot.reporting.mode:MANUAL}")
    private WebScreenshotType desiredMode;

    @Override
    public void onApplicationEvent(WebScreenshotEvent webScreenshotEvent) {
        if (desiredMode.getHierarchy() >= webScreenshotEvent.getType().getHierarchy()){
            screenshotCreator.createScreenshot(webScreenshotEvent.getType());
        }
    }

}
