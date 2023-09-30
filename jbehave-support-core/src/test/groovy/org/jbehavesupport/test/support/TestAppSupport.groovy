package org.jbehavesupport.test.support

trait TestAppSupport {

    public static TestAppContainer testAppContainer = new TestAppContainer();

    static {
        testAppContainer.start();
    }

    int getUiPort() {
        testAppContainer.getMappedPort(11110)
    }

}
