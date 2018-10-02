package org.jbehavesupport.core

import groovy.transform.EqualsAndHashCode
import lombok.Data
import org.jbehavesupport.core.internal.TestContextImpl
import org.jbehavesupport.core.support.RequestFactory
import org.jbehavesupport.core.test.app.oxm.NameRequest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.support.ConfigurableConversionService
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import javax.activation.DataHandler
import javax.xml.bind.JAXBElement
import javax.xml.datatype.DatatypeConstants
import javax.xml.datatype.DatatypeFactory
import javax.xml.datatype.XMLGregorianCalendar
import java.time.*
import java.time.format.DateTimeParseException
import java.util.function.Consumer

import static groovy.test.GroovyAssert.shouldFail
import static groovy.test.GroovyAssert.shouldFailWithCause
import static org.jbehavesupport.core.internal.MetadataUtil.type
import static org.junit.Assert.fail

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig)
class RequestFactoryTest {

    TestContext ctx = new TestContextImpl()

    @Autowired
    ConfigurableConversionService conversionService;

    @Test
    void shouldCreateRequest() {
        ctx.put("Foo.nil", null)
        ctx.put("Foo.string", "abc")
        ctx.put("Foo.bool1", "true")
        ctx.put("Foo.bool2", "false")
        ctx.put("Foo.int1", "11")
        ctx.put("Foo.int2", "22")
        ctx.put("Foo.long1", "111")
        ctx.put("Foo.long2", "222")
        ctx.put("Foo.bigDecimal", "1.23")
        ctx.put("Foo.bigInteger", "123")
        ctx.put("Foo.localDate", "2017-02-14")
        ctx.put("Foo.localDatetime", "2016-01-13T15:22:39")
        ctx.put("Foo.zonedDateTime", "2015-12-30T14:21:28+02:00")
        ctx.put("Foo.xmlGregorianCalendar1", "2017-02-14")
        ctx.put("Foo.xmlGregorianCalendar2", "2017-02-14T11:12:00")
        ctx.put("Foo.xmlGregorianCalendar3", "2017-02-14T11:12:13+01:00")
        ctx.put("Foo.barList.0.text", "a")
        ctx.put("Foo.barList.1.text", "b")
        ctx.put("Foo.barSet.0.text", "c")
        ctx.put("Foo.barSet.1.text", "d")
        ctx.put("Foo.stringList.0", "item 1")
        ctx.put("Foo.stringList.1", "item 2")
        ctx.put("Foo.abar", null, type("org.jbehavesupport.core.Bar"))
        ctx.put("Foo.abar.text", "barr")
        ctx.put("Foo.abarList.0", null, type("org.jbehavesupport.core.Bar"))
        ctx.put("Foo.abarList.0.text", "barr")
        ctx.put("Foo.nestList.barNestedList.0.text", "veryNested")

        def nestList = new NestedList();
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
    public void shouldOverride() throws Exception {
        ctx.put("Foo.bool2", "false")
        ctx.put("Foo.myKey", "11")

        def fooExpected = new Foo(
            bool2: false,
            int1: 11)

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.override("myKey", "int1")

        def request = requestFactory.createRequest()
        assert request == fooExpected
    }

    @Test
    public void shouldUseCustomPrefix() throws Exception {
        ctx.put("MyClass.bool2", "false")
        ctx.put("MyClass.int1", "11")

        def fooExpected = new Foo(
            bool2: false,
            int1: 11)

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.prefix("MyClass")

        def request = requestFactory.createRequest()
        assert request == fooExpected
    }

    @Test
    public void shouldUseCustomHandler() throws Exception {
        ctx.put("Foo.abar.text", "should be handled instead")

        def fooExpected = new Foo(abar: new Bar(text: "barr"))

        def requestFactory = new RequestFactory(Foo, ctx, conversionService)
        requestFactory.handler("Foo.abar.text", new Consumer<Foo>() {
            @Override
            void accept(final Foo foo) {
                Abar a = new Bar();
                a.setText("barr")
                foo.setAbar(a)
            }
        })

        def request = requestFactory.createRequest()
        assert request == fooExpected
    }

    @Test
    void shouldFailWithExplanation() {
        ctx.put("Foo.bug", "")

        try {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
            fail()
        } catch (Exception e) {
            assert e.message.contains("Unable to create request Foo")
            assert e.cause.message.contains("There is no property bug in class Foo")
        }
    }

    @Test
    void shouldNotInstantiateAbstract() {
        ctx.put("Foo.abar.text", "barr")
        try {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
            fail()
        } catch (Exception e) {
            assert e.message.contains("Unable to create request Foo")
            assert e.cause.message.contains("Field Foo.abar is abstract. Please provide fully qualified class name in example table or register custom handler")
        }
    }

    @Test
    void shouldMissImplementationOfAbstract() {
        String message = shouldFail(IllegalArgumentException.class) {
            ctx.put("Foo.abar", null, type("wrong.class.path"))
        }
        assert message.contains("Provided class not found")
    }

    @Test
    void shouldMissField() throws Exception {
        ctx.put("Foo.mySecretField.value", "19")

        String message = shouldFailWithCause(NoSuchElementException.class) {
            new RequestFactory(Foo, ctx, conversionService).withFieldAccessStrategy().createRequest()
        }

        assert message.contains("There is no field mySecretField in class Foo, please check keys in test story")
    }

    @Test
    void shouldFailOnAbstractInstantiation() {
        ctx.put("Foo.abar", null, type("org.jbehavesupport.core.Abar"))

        String message = shouldFailWithCause(IllegalStateException.class) {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }
        assert message.contains("Provided implementation could not be instantiated")
    }

    @Test
    void shouldNotParseWrongTime() throws Exception {
        ctx.put("Foo.localDate", "tomorrow")

        String message = shouldFailWithCause(DateTimeParseException.class) {
            new RequestFactory(Foo, ctx, conversionService).createRequest()
        }
        assert message.contains("Text 'tomorrow' could not be parsed")
    }

    @Test
    void shouldUseConversionService() {
        ctx.put("Foo.dataHandler", "<foo>bar</foo>")

        def conversionService = new DefaultConversionService()
        conversionService.addConverter(new Converter<String, DataHandler>() {
            @Override
            DataHandler convert(String source) {
                return new DataHandler(source, "application/xml")
            }
        })

        def foo = RequestFactory.newInstance(Foo, ctx, conversionService).createRequest()

        assert foo.dataHandler != null
        assert foo.dataHandler.content == "<foo>bar</foo>"
        assert foo.dataHandler.contentType == "application/xml"
    }

    @Test
    public void shouldTestAccessStrategies() throws Exception {
        ctx.put("NameRequest.CUID", "1234")
        //pass with bean
        new RequestFactory(NameRequest, ctx, conversionService).createRequest();

        //fails with field
        String message = shouldFailWithCause(NoSuchElementException.class) {
            new RequestFactory(NameRequest, ctx, conversionService).withFieldAccessStrategy().createRequest();
        }

        assert message.contains("There is no field CUID in class NameRequest, please check keys in test story")
    }

    @Test
    public void shouldFailOnMultipleFields() throws Exception {
        ctx.put("Ugly.attribute", "{nil}")
        ctx.put("Ugly.AtTribute", "456}")

        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(Ugly, ctx, conversionService).withFieldAccessStrategy().createRequest();
        }

        assert message.contains("multiple fields match name condition")
    }

    @Test
    public void shouldDetectNestedGenerics() {
        ctx.put("UnsupportedClass.listInList.0", "0-0")

        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(UnsupportedClass, ctx, conversionService).withFieldAccessStrategy().createRequest();
        }

        assert message.contains("Nested generic types are not supported")
    }

    @Test
    public void shouldDetectAbstracGenerics() {
        ctx.put("UnsupportedClass.abstractList.0.text", "inputTxt")

        String message = shouldFailWithCause(UnsupportedOperationException.class) {
            new RequestFactory(UnsupportedClass, ctx, conversionService).withFieldAccessStrategy().createRequest();
        }

        assert message.contains("Abstract classes are not supported as generic")
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
    }

    @Data
    @EqualsAndHashCode
    static class NestedList {
        List<Bar> barNestedList
    }

    static class Ugly {
        JAXBElement<String> attribute;
        JAXBElement<Integer> AtTribute;
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
class UnsupportedClass {

    List<List<String>> listInList;
    List<Abar> abstractList;

}
