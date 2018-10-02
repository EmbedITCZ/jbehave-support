package org.jbehavesupport.test.support


import org.jbehavesupport.test.GenericStory

trait TestSupport {

    Class runWith(String storyFile) {
        GenericStory.STORY_FILE = "org/jbehavesupport/test/" + storyFile
        return GenericStory
    }
}
