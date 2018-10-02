package org.jbehavesupport.core.healthcheck;

/**
 * The interface represents health-check operation,
 * implementations should throw exception if the checked system is not healthy.
 */
public interface HealthCheck {

    void check();

}
