package org.jbehavesupport.core.internal.splunk;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jbehavesupport.core.splunk.SplunkSearchResultEntry;

@AllArgsConstructor
public class SplunkSearchContext {
    @Getter
    private List<SplunkSearchResultEntry> searchResults;
    @Getter
    private String query;
}
