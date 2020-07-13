package org.jbehavesupport.core.splunk;

import com.splunk.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jbehavesupport.core.internal.splunk.SplunkEventFields;

@Getter
@EqualsAndHashCode
@ToString
public class SplunkSearchResultEntry {
    private String time;
    private String level;
    private String message;
    private String namespace;
    private String index;
    private String host;

    public SplunkSearchResultEntry(Event event) {
        this.time = event.get(SplunkEventFields.TIME);
        this.level = event.get(SplunkEventFields.LEVEL);
        this.message = event.get(SplunkEventFields.MESSAGE);
        this.namespace = event.get(SplunkEventFields.NAMESPACE);
        this.index = event.get(SplunkEventFields.INDEX);
        this.host = event.get(SplunkEventFields.HOST);
    }
}
