package org.jbehavesupport.core

import groovy.transform.EqualsAndHashCode
import lombok.Data
import org.jbehave.core.model.ExamplesTable
import org.jbehavesupport.core.internal.parameterconverters.ExamplesEvaluationTableConverter
import org.jbehavesupport.core.support.RequestFactory
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.jbehavesupport.core.ws.WebServiceSteps
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import jakarta.activation.DataHandler
import jakarta.xml.bind.JAXBElement
import javax.xml.datatype.DatatypeConstants
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import java.time.*
import java.time.format.DateTimeParseException
import java.util.function.Consumer

import static groovy.test.GroovyAssert.shouldFail
import static groovy.test.GroovyAssert.shouldFailWithCause

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig)
class RequestFactoryTest {

    @Autowired
    TestContext ctx

    @Autowired
    ConfigurableConversionService conversionService

    @Autowired
    ExamplesEvaluationTableConverter etFactory

    @Autowired
    WebServiceSteps webServiceSteps

    @Before
    void init() {
        ctx.clear()
    }

    @Test
    void "Should create request"() {
        def etData = etFactory.convertValue("" +
            "| name                          | data                      | type                        |\n" +
            "| string                        | abc                       |                             |\n" +
            "| bool1                         | true                      |                             |\n" +
            "| bool2                         | false                     |                             |\n" +
            "| int1                          | 11                        |                             |\n" +
            "| int2                          | 22                        |                             |\n" +
            "| long1                         | 111                       |                             |\n" +
            "| long2                         | 222                       |                             |\n" +
            "| bigDecimal                    | 1.23                      |                             |\n" +
            "| bigInteger                    | 123                       |                             |\n" +
            "| localDate                     | 2017-02-14                |                             |\n" +
            "| localDatetime                 | 2016-01-13T15:22:39       |                             |\n" +
            "| zonedDateTime                 | 2015-12-30T14:21:28+02:00 |                             |\n" +
            "| xmlGregorianCalendar1         | 2017-02-14                |                             |\n" +
            "| xmlGregorianCalendar2         | 2017-02-14T11:12:00       |                             |\n" +
            "| xmlGregorianCalendar3         | 2017-02-14T11:12:13+01:00 |                             |\n" +
            "| barList.0.text                | a                         |                             |\n" +
            "| barList.1.text                | b                         |                             |\n" +
            "| barSet.0.text                 | c                         |                             |\n" +
            "| barSet.1.text                 | d                         |                             |\n" +
            "| stringList.0                  | item 1                    |                             |\n" +
            "| stringList.1                  | item 2                    |                             |\n" +
            "| abar                          |                           | org.jbehavesupport.core.Bar |\n" +
            "| abar.text                     | barr                      |                             |\n" +
            "| abarList.0                    |                           | org.jbehavesupport.core.Bar |\n" +
            "| abarList.0.text               | barr                      |                             |\n" +
            "| nestList.barNestedList.0.text | veryNested                |                             |",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        def nestList = new NestedList()
        nestList.setBarNestedList([new Bar(text: "veryNested")])

        def fooExpected = new Foo(
            nil: null,
            string: "abc",
            bool1: true,
            bool2: false,
            int1: 11,
            int2: 22,
            long1: 111,
            long2: 222,
            bigDecimal: BigDecimal.valueOf(1.23),
            bigInteger: BigInteger.valueOf(123),
            localDate: LocalDate.of(2017, 2, 14),
            localDatetime: LocalDateTime.of(2016, 1, 13, 15, 22, 39),
            zonedDateTime: ZonedDateTime.of(LocalDate.of(2015, 12, 30), LocalTime.of(14, 21, 28), ZoneId.of("+02:00")),
            xmlGregorianCalendar1: DatatypeFactory.newInstance().newXMLGregorianCalendarDate(2017, 2, 14, DatatypeConstants.FIELD_UNDEFINED),
            xmlGregorianCalendar2: DatatypeFactory.newInstance().newXMLGregorianCalendar(2017, 2, 14, 11, 12, 00, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED),
            xmlGregorianCalendar3: DatatypeFactory.newInstance().newXMLGregorianCalendar(2017, 2, 14, 11, 12, 13, DatatypeConstants.FIELD_UNDEFINED, 60),
            barList: [new Bar(text: "a"), new Bar(text: "b")],
            barSet: [new Bar(text: "c"), new Bar(text: "d")] as LinkedHashSet,
            stringList: ["item 1", "item 2"],
            abar: new Bar(text: "barr"),
            abarList: [new Bar(text: "barr")],
            nestList: nestList
        )

        def fooActual = new RequestFactory(Foo, ctx, conversionService).createRequest()
        assert fooActual == fooExpected

        fooActual = new RequestFactory(Foo, ctx, conversionService).withFieldAccessStrategy().createRequest()
        assert fooActual == fooExpected
    }

    @Test
    void "Should override"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name  | data  |\n" +
            "| bool2 | false |\n" +
            "| myKey | 11    |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        def fooExpected = new Foo(
            bool2: false,
            int1: 11)

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.override("myKey", "int1")

        when:
        def fooActual = requestFactory.createRequest()

        then:
        fooActual == fooExpected
    }

    @Test
    void "Should use custom prefix"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name  | data  |\n" +
            "| bool2 | false |\n" +
            "| int1  | 11    |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("MyClass", "TEST", etData)

        def fooExpected = new Foo(
            bool2: false,
            int1: 11)

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.prefix("MyClass")

        when:
        def fooActual = requestFactory.createRequest()

        then:
        fooActual == fooExpected
    }

    @Test
    void "Should use custom handler"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name      | data                      |\n" +
            "| abar.text | should be handled instead |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        def fooExpected = new Foo(abar: new Bar(text: "barr"))

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.handler("Foo.abar.text", new Consumer<Foo>() {
            @Override
            void accept(final Foo foo) {
                Abar a = new Bar()
                a.setText("barr")
                foo.setAbar(a)
            }
        })

        when:
        def fooActual = requestFactory.createRequest()

        then:
        fooActual == fooExpected
    }

    @Test
    void "Should fail with explanation"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name | data  |\n" +
            "| bug  |       |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        def exception = shouldFail() {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }

        then:
        assert exception.message.contains("Unable to create request Foo")
        assert exception.cause.message.contains("There is no property bug in class Foo")
    }

    @Test
    void "Should not instantiate abstract"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name      | data |\n" +
            "| abar.text | barr |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        def exception = shouldFail() {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }

        then:
        exception.message.contains("Unable to create request Foo")
        exception.cause.message.contains("Field Foo.abar is abstract. Please provide fully qualified class name in example table or register custom handler")
    }

    @Test
    void "Should miss implementation of abstract"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name | data | type             |\n" +
            "| abar |      | wrong.class.path |\n",
            null) as ExamplesTable

        when:
        String message = shouldFail(IllegalArgumentException.class) {
            webServiceSteps.requestData("Foo", "TEST", etData)
        }

        then:
        assert message.contains("Provided class not found")
    }

    @Test
    void "Should miss field"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name                | data |\n" +
            "| mySecretField.value | 19   |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        String message = shouldFailWithCause(NoSuchElementException.class) {
            new RequestFactory(Foo, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }

        then:
        message.contains("There is no field mySecretField in class Foo, please check keys in test story")
    }

    @Test
    void "Should fail on abstract instantiation"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name | data | type             |\n" +
            "| abar |      | org.jbehavesupport.core.Abar |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        String message = shouldFailWithCause(IllegalStateException.class) {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }
        then:
        message.contains("Provided implementation could not be instantiated")
    }

    @Test
    void "Should not parse wrong time"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name      | data     |\n" +
            "| localDate | tomorrow |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        String message = shouldFailWithCause(DateTimeParseException.class) {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }

        then:
        message.contains("Text 'tomorrow' could not be parsed")
    }

    @Test
    void "Should use conversion service"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name        | data           |\n" +
            "| dataHandler | <foo>bar</foo> |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        def conversionService = new DefaultConversionService()
        conversionService.addConverter(new Converter<String, DataHandler>() {
            @Override
            DataHandler convert(String source) {
                return new DataHandler(source, "application/xml")
            }
        })

        when:
        def foo = RequestFactory.newInstance(Foo, ctx, conversionService).createRequest()

        then:
        foo.dataHandler != null
        foo.dataHandler.content == "<foo>bar</foo>"
        foo.dataHandler.contentType == "application/xml"
    }

    @Test
    void "Should test access strategies"() throws Exception {
        def etData = etFactory.convertValue("" +
            "| name | data |\n" +
            "| CUID | 1234 |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("NameRequest", "TEST", etData)

        //pass with bean
        new RequestFactory(NameRequest, ctx, conversionService).createRequest()

        //fails with field
        String message = shouldFailWithCause(NoSuchElementException.class) {
            new RequestFactory(NameRequest, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }
        assert message.contains("There is no field CUID in class NameRequest, please check keys in test story")
    }

    @Test
    void "Should fail on multiple fields"() throws Exception {
        given:
        def etData = etFactory.convertValue("" +
            "| name      | data  |\n" +
            "| attribute | {NIL} |\n" +
            "| AtTribute | 456   |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Ugly", "TEST", etData)

        when:
        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(Ugly, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }

        then:
        message.contains("multiple fields match name condition")
    }

    @Test
    void "Should detect nested generics"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name      | data  |\n" +
            "| attribute | {NIL} |\n" +
            "| AtTribute | 456   |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Ugly", "TEST", etData)

        ctx.put("UnsupportedClass.listInList.0", "0-0")

        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(UnsupportedClass, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }

        assert message.contains("Nested generic types are not supported")
    }

    @Test
    void "Should detect abstract generics"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name                | data     |\n" +
            "| abstractList.0.text | inputTxt |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("UnsupportedClass", "TEST", etData)

        when:
        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(UnsupportedClass, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }

        then:
        message.contains("Abstract classes are not supported as generic")
    }

    @Test
    void "Should accept user type before explicit one"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name           | data   | type                             |\n" +
            "| knownBar       | noRole | org.jbehavesupport.core.ChildBar |\n" +
            "| knownBar.nummy | 99     |                                  |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        Foo fooActual = new RequestFactory(Foo, ctx, conversionService).createRequest()

        then:
        fooActual.getKnownBar().getClass() == ChildBar.class
        ((ChildBar) fooActual.getKnownBar()).getNummy() == 99L
    }

    @Test
    void "Should refuse user type, that is not applicable"() {
        given:
        def etData = etFactory.convertValue("" +
            "| name           | data   | type                                     |\n" +
            "| knownBar       | noRole | org.jbehavesupport.core.UnsupportedClass |\n" +
            "| knownBar.nummy | 99     |                                          |\n",
            null) as ExamplesTable
        webServiceSteps.requestData("Foo", "TEST", etData)

        when:
        String message = shouldFailWithCause(IllegalStateException.class) {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }

        then:
        true
        message.contains("Failed to convert property value of type 'org.jbehavesupport.core.UnsupportedClass'")
    }

    @Data
    @EqualsAndHashCode
    static class Foo {
        Object nil
        String string
        boolean bool1
        Boolean bool2
        int int1
        Integer int2
        long long1
        Long long2
        BigDecimal bigDecimal
        BigInteger bigInteger
        LocalDate localDate
        LocalDateTime localDatetime
        ZonedDateTime zonedDateTime
        XMLGregorianCalendar xmlGregorianCalendar1
        XMLGregorianCalendar xmlGregorianCalendar2
        XMLGregorianCalendar xmlGregorianCalendar3
        List<Bar> barList
        Set<Bar> barSet
        List<String> stringList
        DataHandler dataHandler
        Abar abar
        List<Abar> abarList
        NestedList nestList
        Bar knownBar
    }

    @Data
    @EqualsAndHashCode
    static class NestedList {
        List<Bar> barNestedList
    }

    static class Ugly {
        JAXBElement<String> attribute
        JAXBElement<Integer> AtTribute
    }
}

@Data
@EqualsAndHashCode
abstract class Abar {
}

@Data
@EqualsAndHashCode
class Bar extends Abar {
    String text
}

@Data
@EqualsAndHashCode
class ChildBar extends Bar {
    Long nummy
}

@Data
class UnsupportedClass {

    List<List<String>> listInList
    List<Abar> abstractList

}
