package org.jbehavesupport.test.support

import java.time.LocalDate

trait TestSupportBrowserStack extends TestSupport {

    String getBuildName() {
        return LocalDate.now().toString()
    }

}
