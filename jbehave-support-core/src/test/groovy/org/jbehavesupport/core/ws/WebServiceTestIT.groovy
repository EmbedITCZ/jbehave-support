package org.jbehavesupport.core.ws

import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.test.app.domain.Address
import org.jbehavesupport.core.test.app.oxm.AddressInfo
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.jbehavesupport.core.test.app.oxm.NameResponse
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class WebServiceTestIT extends Specification{

    @Autowired
    private WebServiceHandler webServiceHandler

    private NameRequest request

    @Test
    void canUseNestedListsWithDifferentNotations() {
        given:
        ExamplesTable data = new ExamplesTable(
            "| name                                  | data  |\n" +
            "| addressList.addressInfo.0.city        | Praha |\n" +
            "| addressList.addressInfo[0].zip        | 11000 |\n" +
            "| addressList.addressInfo[1].city       | Brno  |\n" +
            "| addressList.addressInfo.1.zip         | 60200 |\n" +
            "| addressList.addressInfo.0.details.0   | 00    |\n" +
            "| addressList.addressInfo[0].details[1] | 01    |\n" +
            "| addressList.addressInfo.1.details[0]  | 10    |\n" +
            "| addressList.addressInfo[1].details.1  | 11    |"
        )

        when:
        webServiceHandler.setRequestData("NameRequest", data)
        request = webServiceHandler.createRequest(NameRequest.class, null)

        then:
        noExceptionThrown()
        List<AddressInfo> addressInfoList = request.getAddressList().getAddressInfo()
        addressInfoList[0].city == "Praha"
        addressInfoList[0].zip == "11000"
        addressInfoList[0].details[0] == "00"
        addressInfoList[0].details[1] == "01"
        addressInfoList[1].city == "Brno"
        addressInfoList[1].zip == "60200"
        addressInfoList[1].details[0] == "10"
        addressInfoList[1].details[1] == "11"
    }
}
