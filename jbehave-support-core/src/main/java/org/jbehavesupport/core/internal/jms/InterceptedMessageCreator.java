package org.jbehavesupport.core.internal.jms;

import lombok.RequiredArgsConstructor;
import org.jbehavesupport.core.report.extension.JmsXmlReporterExtension;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@RequiredArgsConstructor
public class InterceptedMessageCreator implements MessageCreator {

    private final MessageCreator messageCreator;

    private final JmsXmlReporterExtension jmsXmlReporterExtensions;

    public Message createMessage(Session session) throws JMSException {
        Message message = messageCreator.createMessage(session);
        jmsXmlReporterExtensions.report(message);
        return message;
    }
}
