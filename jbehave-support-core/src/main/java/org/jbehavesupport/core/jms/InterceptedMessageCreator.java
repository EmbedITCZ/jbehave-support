package org.jbehavesupport.core.jms;

import org.jbehavesupport.core.report.extension.JmsXmlReporterExtension;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class InterceptedMessageCreator implements MessageCreator {

    private MessageCreator messageCreator;

    private JmsXmlReporterExtension jmsXmlReporterExtensions;

    InterceptedMessageCreator(MessageCreator inputMessageCreator, JmsXmlReporterExtension reporter) {
        jmsXmlReporterExtensions = reporter;
        messageCreator = inputMessageCreator;
    }

    public Message createMessage(Session session) throws JMSException {
        Message message = messageCreator.createMessage(session);
        jmsXmlReporterExtensions.report(message);
        return message;
    }
}
