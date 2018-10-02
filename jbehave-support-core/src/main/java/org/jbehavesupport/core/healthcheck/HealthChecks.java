package org.jbehavesupport.core.healthcheck;

import org.jbehavesupport.core.internal.healthcheck.HttpHealthCheckImpl;

import lombok.experimental.UtilityClass;

/**
 * This class contains factory methods for various health-checks.
 */
@UtilityClass
public final class HealthChecks {

    public static HealthCheck http(String url, String username, String password) {
        return new HttpHealthCheckImpl(url, username, password);
    }

}
