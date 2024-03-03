package org.jbehavesupport.test.support

import org.jbehavesupport.test.GenericStory
import org.junit.platform.launcher.LauncherDiscoveryRequest

import java.time.LocalDate

trait TestSupportBrowserStack extends TestSupport {

    String getBuildName() {
        return LocalDate.now().toString()
    }

    LauncherDiscoveryRequest runWith(String storyFile) {
        def request = super.runWith(storyFile)
        // browserstack tests are the only one to use different folder in groovy tests
        GenericStory.STORY_FILE = "org/jbehavesupport/test/" + storyFile
        request
    }

}
