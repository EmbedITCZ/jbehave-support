package org.jebehavesupport.test.bdd.jbehave.sms;

import java.util.Collections;
import java.util.List;

import org.jebehavesupport.test.bdd.AbstractBddStories;
import org.jebehavesupport.test.bdd.annotation.Metafilter;

import org.springframework.test.context.TestPropertySource;

/**
 * Running stories for SMSs WS api v1
 */
@Metafilter("all('api','v1')")
@TestPropertySource(properties = {
    "mss.api.version=v1"
})
public class SendSmsV1Story extends AbstractBddStories {
    public SendSmsV1Story() {
        super(
            SendSmsSteps.class
        );
    }

    @Override
    public List<String> storyPaths() {
        return Collections.singletonList("stories/SendSms.story");
    }
}
