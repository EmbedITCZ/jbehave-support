package org.jbehavesupport.core.ws

import org.jbehave.core.configuration.MostUsefulConfiguration
import org.jbehave.core.model.ExamplesTable
import org.jbehave.core.steps.ParameterConverters
import org.jbehavesupport.core.TestConfig
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter
import org.jbehavesupport.core.internal.parameterconverters.NullStringConverter
import org.jbehavesupport.core.test.app.oxm.AddressInfo
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = TestConfig)
class WebServiceTestIT extends Specification{

    @Autowired
    private WebServiceHandler webServiceHandler

    @Autowired
    private ExamplesEvaluationTableConverter converter

    @Autowired
    private NullStringConverter nullStringConverter

    private NameRequest request

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

    void canFillEmptyElement() {
        given:
        def paramsConverters = new ParameterConverters().addConverters(nullStringConverter)
        converter.setConfiguration(new MostUsefulConfiguration().useParameterConverters(paramsConverters))

        def tableData =
            "| name                           | data   |\n" +
            "| addressList.addressInfo.0.city | Praha  |\n" +
            "| cell                           | {NULL} |"
        ExamplesTable data = converter.convertValue(tableData, ExamplesTable.class)

        when:
        webServiceHandler.setRequestData("NameRequest", data)
        request = webServiceHandler.createRequest(NameRequest.class, null)

        then:
        noExceptionThrown()
        request.addressList.addressInfo[0].city == "Praha"
        request.cell != null
        request.name == null
    }

}
