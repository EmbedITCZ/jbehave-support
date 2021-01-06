package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.jbehavesupport.core.report.ReportContext;
import org.jbehavesupport.core.internal.splunk.SplunkSearchContext;
import org.jbehavesupport.core.splunk.SplunkSearchResultEntry;

public class SplunkXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String SPLUNK_XML_REPORTER_EXTENSION = "splunk";
    private static final String SEARCH_RESULT_TAG = "searchResult";
    private static final String TIME_TAG = "time";
    private static final String QUERY_TAG = "query";
    private static final String MESSAGE_TAG = "message";
    private static final String RECORD_TAG = "record";

    private final List<SplunkSearchContext> searchResults = new ArrayList<>();

    @Override
    public String getName() {
        return SPLUNK_XML_REPORTER_EXTENSION;
    }

    @Override
    public void print(Writer writer, ReportContext reportContext) {
        searchResults.forEach(query -> printSplunkSearchResult(writer, query));
        searchResults.clear();
    }

    public void addSplunkSearchContext(SplunkSearchContext splunkSearchContext) {
        searchResults.add(splunkSearchContext);
    }

    private void printSplunkSearchResult(Writer writer, SplunkSearchContext splunkSearchContext) {
        printBegin(writer, SEARCH_RESULT_TAG);
        printBegin(writer, QUERY_TAG);
        printCData(writer, splunkSearchContext.getQuery());
        printEnd(writer, QUERY_TAG);

        for(SplunkSearchResultEntry searchResult : splunkSearchContext.getSearchResults()){
            printBegin(writer, RECORD_TAG);
            printBegin(writer, TIME_TAG);
            printCData(writer, searchResult.getTime());
            printEnd(writer, TIME_TAG);
            printBegin(writer, MESSAGE_TAG);
            printCData(writer, searchResult.getMessage());
            printEnd(writer, MESSAGE_TAG);
            printEnd(writer, RECORD_TAG);
        }

        printEnd(writer, SEARCH_RESULT_TAG);
    }
}
