package org.jbehavesupport.core.report.extension;

import lombok.extern.slf4j.Slf4j;
import org.jbehavesupport.core.report.ReportContext;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class JmsXmlReporterExtension extends AbstractXmlReporterExtension {

    private static final String JMS_XML_REPORTER_EXTENSION = "jms";
    private static final String MESSAGE_TAG = "message";
    private static final String CID_TAG = "cid";
    private static final String ANSWERS_TAG = "answers";
    private static final String ROW_TAG = "row";
    private static final String COLUMN_TAG = "column";
    private static final String STRING_TAG = "toString";

    private static final String[] headers = {"JMSMessageID", "JMSDestination", "JMSDeliveryMode", "JMSExpiration", "JMSPriority", "JMSTimestamp", "JMSReplyTo", "JMSType", "JMSRedelivered"};

    private final List<Message> messages = new ArrayList();

    @Override
    public String getName() {
        return JMS_XML_REPORTER_EXTENSION;
    }

    public void report(Message message) {
        messages.add(message);
    }

    @Override
    public void print(Writer writer, ReportContext reportContext) {
        messages.forEach(message -> printJmsMessage(writer, message));
        messages.clear();
    }

    private void printJmsMessage(Writer writer, Message message) {
        printBegin(writer, MESSAGE_TAG);

        String cid;
        try {
            cid = message.getJMSCorrelationID();
        } catch (JMSException e) {
            cid = "00000";
            log.error("Unable to get Correlation ID", e);
        }
        printCID(writer, cid);

        printBegin(writer, ANSWERS_TAG);
        printAnswers(writer, message);

        printBegin(writer, STRING_TAG);
        printTextMessage(writer, message);
        printEnd(writer, STRING_TAG);

        printEnd(writer, ANSWERS_TAG);
        printEnd(writer, MESSAGE_TAG);
    }

    private void printTextMessage(Writer writer, Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                printCData(writer, textMessage.getText());
            } catch (JMSException e) {
                log.error("Unable to print TextMessage", e);
                printCData(writer, "Unable to print TextMessage");
            }
        } else {
            printCData(writer, "Not a TextMessage");
        }
    }

    private void printAnswers(Writer writer, Message message) {
        Arrays.stream(headers).forEach(header -> {
            printBegin(writer, ROW_TAG);
            printBegin(writer, COLUMN_TAG);
            printCData(writer, header);
            printEnd(writer, COLUMN_TAG);

            printBegin(writer, COLUMN_TAG);
            try {
                printCData(writer, message.getStringProperty(header));
            } catch (JMSException e) {
                log.error("Unable to get the property: {}", header, e);
                printCData(writer, "Unable to get property: " + header);
            }
            printEnd(writer, COLUMN_TAG);

            printEnd(writer, ROW_TAG);
        });
    }

    private void printCID(Writer writer, String jmsCorrelationID) {
        printBegin(writer, CID_TAG);
        printCData(writer, jmsCorrelationID);
        printEnd(writer, CID_TAG);
    }
}
