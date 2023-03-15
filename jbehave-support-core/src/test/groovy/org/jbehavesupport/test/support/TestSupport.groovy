package org.jbehavesupport.test.support


import org.jbehavesupport.test.GenericStory
import org.junit.platform.launcher.Launcher
import org.junit.platform.launcher.LauncherDiscoveryRequest
import org.junit.platform.launcher.LauncherSession
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.listeners.SummaryGeneratingListener
import org.junit.platform.launcher.listeners.TestExecutionSummary
import spock.lang.AutoCleanup
import spock.lang.Shared

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass

trait TestSupport {

    @Shared
    @AutoCleanup
    LauncherSession session = LauncherFactory.openSession()

    LauncherDiscoveryRequest runWith(String storyFile) {
        GenericStory.STORY_FILE = "org/jbehavesupport/core/" + storyFile

        LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(GenericStory))
            .build()
    }

    TestExecutionSummary run(LauncherDiscoveryRequest request) {
        SummaryGeneratingListener listener = new SummaryGeneratingListener()
        Launcher launcher = session.getLauncher()
        launcher.execute(request, listener)
        listener.getSummary()
    }

}
