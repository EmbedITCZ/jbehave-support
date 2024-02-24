package org.jbehavesupport.test.support;

import org.testcontainers.containers.FixedHostPortGenericContainer;

// we have to use fixed port, since there seem to be some context clashes and @DynamicPropertySource does not seem to properly get to every context as it should
public class TestAppContainer extends FixedHostPortGenericContainer<TestAppContainer> {

    private static final String IMAGE_VERSION = "docker.io/library/jbehave-support-core-test-app:latest";
    private static TestAppContainer TEST_APP_CONTAINER;

    private TestAppContainer() {
        super(IMAGE_VERSION);
    }

    public static TestAppContainer getTestAppContainer() {
        if (TEST_APP_CONTAINER == null) {
            TEST_APP_CONTAINER = new TestAppContainer();
            TEST_APP_CONTAINER.withFixedExposedPort(11110, 11110);
            TEST_APP_CONTAINER.withFixedExposedPort(11112, 11112);
            TEST_APP_CONTAINER.start();
        }
        return TEST_APP_CONTAINER;
    }

    @Override
    public void stop() {
        // no op, let ryuk handle it
    }
}
