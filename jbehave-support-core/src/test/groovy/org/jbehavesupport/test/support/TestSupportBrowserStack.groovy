package org.jbehavesupport.test.support

import org.jbehavesupport.test.GenericStory

import java.time.LocalDate

trait TestSupportBrowserStack {

    Class runWith(String storyFile) {
        GenericStory.STORY_FILE = "org/jbehavesupport/test/" + storyFile
        return GenericStory
    }

    String getBuildName() {
        return LocalDate.now().toString()
    }

}
