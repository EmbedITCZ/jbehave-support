package org.jbehavesupport.test.support


import org.jbehavesupport.test.GenericStory

trait TestSupportBrowserStack {

    Class runWith(String storyFile) {
        GenericStory.STORY_FILE = "org/jbehavesupport/test/" + storyFile
        return GenericStory
    }
}
