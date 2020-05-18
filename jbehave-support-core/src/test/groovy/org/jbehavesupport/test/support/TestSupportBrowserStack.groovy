package org.jbehavesupport.test.support

import com.browserstack.local.Local
import org.jbehavesupport.test.GenericStory

import java.time.LocalDate

trait TestSupportBrowserStack {

    static Local bsLocal = new Local()

    Class runWith(String storyFile) {
        GenericStory.STORY_FILE = "org/jbehavesupport/test/" + storyFile
        return GenericStory
    }

    String getBuildName() {
        return LocalDate.now().toString()
    }

    void setupBrowserStackLocal(String key) {
        if (!bsLocal.isRunning()) {
            HashMap<String, String> bsLocalArgs = new HashMap<String, String>()
            bsLocalArgs.put("key", key)
            bsLocal.start(bsLocalArgs)
        }
    }

    void stopBrowserStackLocal() {
        bsLocal.stop()
    }

}
