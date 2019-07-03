package org.jbehavesupport.core.test.app.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mirror {

    private String httpStatus;
    private Set<Header> headers = new HashSet<>();
    private List<String> payload;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private String key;
        private String value;
    }
}
