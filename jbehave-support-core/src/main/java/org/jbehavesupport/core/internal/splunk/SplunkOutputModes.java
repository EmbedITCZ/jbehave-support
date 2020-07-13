package org.jbehavesupport.core.internal.splunk;

import com.splunk.ResultsReader;
import com.splunk.ResultsReaderCsv;
import com.splunk.ResultsReaderJson;
import com.splunk.ResultsReaderXml;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@Getter
public enum SplunkOutputModes {
    XML("xml") {
        public ResultsReader createReaderFrom(InputStream searchResultStream) throws IOException {
            return new ResultsReaderXml(searchResultStream);
        }
    },
    JSON("json") {
        public ResultsReader createReaderFrom(InputStream searchResultStream) throws IOException {
            return new ResultsReaderJson(searchResultStream);
        }
    },
    CSV("csv") {
        public ResultsReader createReaderFrom(InputStream searchResultStream) throws IOException {
            return new ResultsReaderCsv(searchResultStream);
        }
    };

    private String modeName;
    public abstract ResultsReader createReaderFrom(InputStream searchResultStream) throws IOException;
}
