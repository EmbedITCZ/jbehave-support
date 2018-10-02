package org.jbehavesupport.core.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.TextMessage;

import org.jbehavesupport.core.support.RequestFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.xml.transform.StringResult;

/**
 * Handler which builds on top of {@link JmsHandler}
 * Provides the ability to register JAXB classes
 * Marshals objects of given type using JAXB marshaller and sends JMS text messages within JMS session
 */
public class JmsJaxbHandler extends JmsHandler {

    @Autowired
    private ConversionService conversionService;

    private final Jaxb2Marshaller marshaller;
    private final Map<String, Class> typeAliasToType = new HashMap<>();

    public JmsJaxbHandler(JmsTemplate jmsTemplate, Class... classesToBeBound) {
        super(jmsTemplate);

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(classesToBeBound);
        this.marshaller = jaxb2Marshaller;

        for (Class type : classesToBeBound) {
            typeAliasToType.put(type.getSimpleName(), type);
        }
    }

    @Override
    protected MessageCreator messageCreatorFor(String typeAlias) {
        if (!typeAliasToType.containsKey(typeAlias)) {
            throw new UnknownTypeAliasException("No type was registered for type alias: " + typeAlias);
        }
        Class type = typeAliasToType.get(typeAlias);

        RequestFactory requestFactory = new RequestFactory(type, testContext, conversionService).prefix(typeAlias);
        Object message = requestFactory.createRequest();

        return session -> {
            StringResult result = new StringResult();
            marshaller.marshal(message, result);
            TextMessage textMessage = session.createTextMessage(result.toString());
            handleJmsHeaders(typeAlias, session, textMessage);
            return textMessage;
        };
    }

    private static class UnknownTypeAliasException extends RuntimeException {
        UnknownTypeAliasException(String message) {
            super(message);
        }
    }
}
