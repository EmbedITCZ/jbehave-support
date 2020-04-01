package org.jbehavesupport.core.report.extension;

import java.io.Writer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jbehavesupport.core.report.ReportContext;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringResult;

public class WsXmlReporterExtension extends AbstractXmlReporterExtension implements ClientInterceptor {

    private static final String REQUEST_TIMESTAMP = "WsMessageRecordingInterceptor.requestTimestamp";
    private static final String RESPONSE_TIMESTAMP = "WsMessageRecordingInterceptor.responseTimestamp";
    private static final String WS_XML_REPORTER_EXTENSION = "ws";
    private static final String FAIL = "fail";

    private enum Type {
        REQUEST("request"), RESPONSE("response");

        private final String typeName;

        Type(String typeName) {
            this.typeName = typeName;
        }
    }

    private final Set<MessageContext> messages = new LinkedHashSet<>();
    private final Transformer transformer;

    public WsXmlReporterExtension() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            this.transformer = transformerFactory.newTransformer();
            this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            this.transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            this.transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) {
        messageContext.setProperty(REQUEST_TIMESTAMP, LocalDateTime.now());
        messages.add(messageContext);
        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext) {
        messageContext.setProperty(RESPONSE_TIMESTAMP, LocalDateTime.now());
        messages.add(messageContext);
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext) {
        return handleResponse(messageContext);
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Exception ex) {
        // noop
    }

    @Override
    public String getName() {
        return WS_XML_REPORTER_EXTENSION;
    }

    @Override
    public void print(final Writer writer, final ReportContext reportContext) {
        messages.forEach(message -> printWebServiceMessage(writer, message));
        messages.clear();
    }

    private void printWebServiceMessage(final Writer writer, final MessageContext message) {
        printBegin(writer, "requestResponse");
        WebServiceMessage request = message.getRequest();
        LocalDateTime requestTimestamp = (LocalDateTime) message.getProperty(WsXmlReporterExtension.REQUEST_TIMESTAMP);
        printWebServiceMessage(writer, Type.REQUEST, request, requestTimestamp);

        if (message.hasResponse()) {
            WebServiceMessage response = message.getResponse();
            LocalDateTime responseTimestamp = (LocalDateTime) message.getProperty(WsXmlReporterExtension.RESPONSE_TIMESTAMP);
            printWebServiceMessage(writer, Type.RESPONSE, response, responseTimestamp);
        }
        printEnd(writer, "requestResponse");
    }

    private void printWebServiceMessage(final Writer writer, final Type type, final WebServiceMessage msg, final LocalDateTime timestamp) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("time", timestamp.atZone(ZoneId.systemDefault()).toString());
        String requestName = ((DOMSource) msg.getPayloadSource()).getNode().getLocalName();
        attributes.put("type", requestName);

        printBegin(writer, type.typeName, attributes);
        StringBuilder message = new StringBuilder();
        try {
            SoapHeader soapHeader = ((SoapMessage) msg).getSoapHeader();
            if (soapHeader != null) {
                StringResult headerResult = new StringResult();
                transformer.transform(soapHeader.getSource(), headerResult);
                message.append(headerResult.toString().trim());
            }

            StringResult bodyResult = new StringResult();
            transformer.transform(msg.getPayloadSource(), bodyResult);
            message.append(bodyResult.toString());
        } catch (TransformerException e) {
            printBegin(writer, FAIL);
            printCData(writer, ExceptionUtils.getStackTrace(e));
            printEnd(writer, FAIL);
        }
        printCData(writer, message.toString());
        printEnd(writer, type.typeName);
    }
}
