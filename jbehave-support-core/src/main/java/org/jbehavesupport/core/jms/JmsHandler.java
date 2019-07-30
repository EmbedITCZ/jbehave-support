package org.jbehavesupport.core.jms;

import static lombok.AccessLevel.PROTECTED;
import static org.jbehavesupport.core.support.TestContextUtil.putDataIntoContext;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import lombok.extern.slf4j.Slf4j;
import org.jbehavesupport.core.TestContext;

import lombok.RequiredArgsConstructor;
import org.jbehave.core.model.ExamplesTable;
import org.jbehavesupport.core.report.extension.JmsXmlReporterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.destination.DestinationResolutionException;
import org.springframework.jms.support.destination.DestinationResolver;

/**
 * Handler for JMS related functionality
 * <p>
 * Saves message data to test context
 * <p>
 * Sends JMS messages using {@link JmsTemplate}
 * <p>
 * Handles JMS headers,
 * see <a href="https://docs.oracle.com/cd/E19798-01/821-1841/bnces/index.html">https://docs.oracle.com/cd/E19798-01/821-1841/bnces/index.html</a>
 * <p>
 */
@Slf4j
@RequiredArgsConstructor(access = PROTECTED)
public abstract class JmsHandler {

    private static final String HEADER = ".@header.";
    private static final String JMS_DESTINATION = "JMSDestination";
    private static final String JMS_DELIVERY_MODE = "JMSDeliveryMode";
    private static final String JMS_EXPIRATION = "JMSExpiration";
    private static final String JMS_PRIORITY = "JMSPriority";
    private static final String JMS_MESSAGE_ID = "JMSMessageID";
    private static final String JMS_TIMESTAMP = "JMSTimestamp";
    private static final String JMS_CORRELATION_ID = "JMSCorrelationID";
    private static final String JMS_REPLY_TO = "JMSReplyTo";
    private static final String JMS_REDELIVERED = "JMSRedelivered";
    private static final String JMS_TYPE = "JMSType";

    @Autowired
    protected TestContext testContext;

    @Autowired(required = false)
    private JmsXmlReporterExtension jmsXMLReporterExtension;

    private final JmsTemplate jmsTemplate;

    final void setMessageData(String typeAlias, ExamplesTable data) {
        testContext.clear(key -> key.startsWith(typeAlias));
        putDataIntoContext(testContext, data, typeAlias);
    }

    void sendMessage(String destinationName, String typeAlias) {
        jmsTemplate.send(destinationName, getMessageCreator(messageCreatorFor(typeAlias)));
    }

    protected abstract MessageCreator messageCreatorFor(String typeAlias);

    private MessageCreator getMessageCreator(MessageCreator messageCreator) {
        if (jmsXMLReporterExtension != null) {
            return new InterceptedMessageCreator(messageCreator, jmsXMLReporterExtension);
        } else {
            return messageCreator;
        }
    }

    void handleJmsHeaders(String typeAlias, Session session, Message message) throws JMSException {
        DestinationResolver destinationResolver = jmsTemplate.getDestinationResolver();
        String prefix = typeAlias + HEADER;
        if (testContext.contains(prefix + JMS_DESTINATION)) {
            String jmsDestination = testContext.get(prefix + JMS_DESTINATION, String.class);
            Destination destination;
            try {
                destination = destinationResolver.resolveDestinationName(session, jmsDestination, false);
            } catch (DestinationResolutionException e) {
                destination = destinationResolver.resolveDestinationName(session, jmsDestination, true);
            }
            message.setJMSDestination(destination);
        }

        if (testContext.contains(prefix + JMS_DELIVERY_MODE)) {
            Integer jmsDeliveryMode = testContext.get(prefix + JMS_DELIVERY_MODE, int.class);
            message.setJMSDeliveryMode(jmsDeliveryMode);
        }

        if (testContext.contains(prefix + JMS_EXPIRATION)) {
            Long jmsExpiration = testContext.get(prefix + JMS_EXPIRATION, long.class);
            message.setJMSExpiration(jmsExpiration);
        }

        if (testContext.contains(prefix + JMS_PRIORITY)) {
            Integer jmsPriority = testContext.get(prefix + JMS_PRIORITY, int.class);
            message.setJMSPriority(jmsPriority);
        }

        if (testContext.contains(prefix + JMS_MESSAGE_ID)) {
            String jmsMessageId = testContext.get(prefix + JMS_MESSAGE_ID, String.class);
            message.setJMSMessageID(jmsMessageId);
        }

        if (testContext.contains(prefix + JMS_TIMESTAMP)) {
            Long jmsTimestamp = testContext.get(prefix + JMS_TIMESTAMP, long.class);
            message.setJMSTimestamp(jmsTimestamp);
        }

        if (testContext.contains(prefix + JMS_CORRELATION_ID)) {
            String jmsCorrelationId = testContext.get(prefix + JMS_CORRELATION_ID, String.class);
            message.setJMSCorrelationID(jmsCorrelationId);
        }

        if (testContext.contains(prefix + JMS_REPLY_TO)) {
            String jmsReplyTo = testContext.get(prefix + JMS_REPLY_TO, String.class);

            Destination destination;
            try {
                destination = destinationResolver.resolveDestinationName(session, jmsReplyTo, false);
            } catch (DestinationResolutionException e) {
                destination = destinationResolver.resolveDestinationName(session, jmsReplyTo, true);
            }

            message.setJMSReplyTo(destination);
        }

        if (testContext.contains(prefix + JMS_REDELIVERED)) {
            Boolean jmsRedelivered = testContext.get(prefix + JMS_REDELIVERED, boolean.class);
            message.setJMSRedelivered(jmsRedelivered);
        }

        if (testContext.contains(prefix + JMS_TYPE)) {
            String jmsType = testContext.get(prefix + JMS_TYPE, String.class);
            message.setJMSType(jmsType);
        }
    }

}
