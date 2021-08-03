package org.jbehavesupport.core.splunk;

import com.splunk.Args;
import com.splunk.ResultsReader;
import com.splunk.Service;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jbehavesupport.core.internal.splunk.SplunkArgNames;
import org.jbehavesupport.core.internal.splunk.SplunkOutputModes;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.jbehavesupport.core.internal.splunk.SplunkArgNames.EARLIEST_TIME;
import static org.jbehavesupport.core.internal.splunk.SplunkArgNames.LATEST_TIME;
import static org.jbehavesupport.core.internal.splunk.SplunkArgNames.OUTPUT_MODE;
import static org.jbehavesupport.core.internal.splunk.SplunkOutputModes.JSON;


/**
 * This a class wraps Splunk Java SDK and its "one shot search" API which allows our Splunk steps to communicate with Splunk Search Heads.
 * <p>
 * Prior to your testing, verify that you have a valid Splunk account and you know hostname and port.
 * <p>
 * You can use username/password or auth token if you generated one. All the configuration elements should go to your application yaml file.
 *
 * <p>
 * Example:
 * splunk:
 * host: <YOUR SPLUNK HOST>
 * port: <YOUR SPLUNK HOST PORT>
 * scheme: https
 * credentials:
 * username: admin
 * password: password
 * or
 * token: Bearer <YOUR SPLUNK AUTH TOKEN>
 */

@RequiredArgsConstructor
@Slf4j
public class OneShotSearchSplunkClient implements SplunkClient {

    @NonNull
    private final SplunkConfig config;
    private Service service;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SplunkSearchResultEntry> search(String query, String earliestTime, String latestTime, SplunkOutputModes splunkOutputMode) {
        validateTimes(earliestTime, latestTime);
        try {
            return processSearchResult(getSplunkService().oneshotSearch(
                query,
                toArgs(
                    Arrays.asList(
                        Pair.of(EARLIEST_TIME, earliestTime),
                        Pair.of(LATEST_TIME, latestTime),
                        Pair.of(OUTPUT_MODE, splunkOutputMode.getModeName())
                    )
                )
            ), splunkOutputMode);
        } catch (IOException e) {
            throw new IllegalArgumentException("Splunk search failed", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SplunkSearchResultEntry> search(String query) {
        return search(query, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SplunkSearchResultEntry> search(String query, String earliestTime, String latestTime) {
        return search(query, earliestTime, latestTime, JSON);
    }

    private void validateTimes(String earliestTimeStr, String latestTimeStr) {
        ZonedDateTime earliestTime = nonNull(earliestTimeStr) ? ZonedDateTime.parse(earliestTimeStr) : null;
        ZonedDateTime latestTime = nonNull(latestTimeStr) ? ZonedDateTime.parse(latestTimeStr) : null;

        if ((nonNull(earliestTime) && nonNull(latestTime)) && latestTime.isBefore(earliestTime)) {
            throw new IllegalArgumentException("Latest time cannot be before earliest time");
        }
    }

    private Service getSplunkService() {
        if (isNull(service)) {
            if (nonNull(config.getUsername())) {
                // username/password based service
                service = Service.connect(config.toServiceArguments());
            } else {
                // auth token based service
                service = new Service(config.toServiceArguments());
            }
        }
        return service;
    }

    private List<SplunkSearchResultEntry> processSearchResult(InputStream searchResultStream, SplunkOutputModes splunkOutputMode) throws IOException {
        ResultsReader resultReader = null;
        try {
            resultReader = splunkOutputMode.createReaderFrom(searchResultStream);
            return StreamSupport.stream(resultReader.spliterator(), false)
                .map(SplunkSearchResultEntry::new)
                .collect(Collectors.toList());
        } finally {
            if (!isNull(resultReader)) {
                resultReader.close();
            }
        }
    }

    private Args toArgs(List<Pair<SplunkArgNames, String>> argList) {
        return Args.create(
            argList.stream()
                .filter(entry -> !isNull(entry.getValue()))
                .collect(Collectors.toMap(pair -> pair.getKey().getArgName(), Pair::getValue))
        );
    }
}
