package org.jbehavesupport.core.test.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/services")
public class SplunkController {

    public static final String CONTENT_KEY_SEARCH = "search";
    public static final String SEARCH_NO_DATA = "no data";

    @Value("classpath:splunk/server-info-response.xml")
    Resource mockedServerInfoResponse;

    @Value("classpath:splunk/login-response.xml")
    Resource mockedLoginResponse;

    @Value("classpath:splunk/search-data-response.json")
    Resource mockedSearchDataResponse;

    @Value("classpath:splunk/search-no-data-response.json")
    Resource mockedSearchNoDataResponse;

    @PostMapping(value = "/auth/login",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> login(@RequestParam Map<String, String> body) {
        return new ResponseEntity<>(resourceToString(mockedLoginResponse), HttpStatus.OK);
    }

    @GetMapping(value = "/server/info",
        produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> info() {
        return new ResponseEntity<>(resourceToString(mockedServerInfoResponse), HttpStatus.OK);
    }

    @PostMapping(value = "/search/jobs",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> search(@RequestParam Map<String, String> body) {
        ResponseEntity<String> response = new ResponseEntity<>(resourceToString(mockedSearchDataResponse), HttpStatus.OK);
        if (nonNull(body) && shouldNotReturnSearchDataMatch(body)) {
            response = new ResponseEntity<>(resourceToString(mockedSearchNoDataResponse), HttpStatus.OK);
        }
        return response;
    }

    private boolean shouldNotReturnSearchDataMatch(Map<String, String> body) {
        return body.getOrDefault(CONTENT_KEY_SEARCH, "").contains(SEARCH_NO_DATA);
    }

    private String resourceToString(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resource " + resource.getFilename());
        }
    }
}
