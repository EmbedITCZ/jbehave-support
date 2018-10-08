package org.jbehavesupport.core.ws;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.function.Supplier;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.TransformerFactory;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;
import org.springframework.xml.transform.StringSource;

/**
 * This class provides convenient api for customization of {@link WebServiceTemplate},
 * it is used by {@link WebServiceHandler}.
 * <p>
 * Example:
 * <pre>
 * public class MyAppWebServiceHandler extends WebServiceHandler {
 *
 *     &#064;Override
 *     protected void initTemplate(WebServiceTemplateConfigurer templateConfigurer) {
 *         templateConfigurer
 *             .defaultUri(myUrl)
 *             .authenticatingMessageSender(myUsername, myPassword);
 *     }
 *
 * }
 * </pre>
 */
public class WebServiceTemplateConfigurer {

    private final WebServiceTemplate template;

    public WebServiceTemplateConfigurer(WebServiceTemplate template) {
        this.template = template;
    }

    public final WebServiceTemplateConfigurer defaultUri(String defaultUri) {
        template.setDefaultUri(defaultUri);
        return this;
    }

    public final WebServiceTemplateConfigurer classesToBeBound(Class... classesToBeBound) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(classesToBeBound);
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        return this;
    }

    public final WebServiceTemplateConfigurer authenticatingMessageSender(String username, String password) {
        AuthenticatingUrlConnectionMessageSender authenticatingMessageSender = new AuthenticatingUrlConnectionMessageSender();
        authenticatingMessageSender.setUsername(username);
        authenticatingMessageSender.setPassword(password);
        authenticatingMessageSender.setTrustManagers(new TrustManager[]{new DummyX509TrustManager()});
        try {
            authenticatingMessageSender.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        template.setMessageSender(authenticatingMessageSender);
        return this;
    }

    public final WebServiceTemplateConfigurer header(Supplier<String> headerProvider) {
        ClientInterceptor headerInterceptor = new ClientInterceptorAdapter() {
            @Override
            public boolean handleRequest(MessageContext msg) throws WebServiceClientException {
                try {
                    TransformerFactory.newInstance()
                        .newTransformer()
                        .transform(new StringSource(headerProvider.get()), ((SoapMessage) msg.getRequest()).getSoapHeader().getResult());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        };
        return interceptor(headerInterceptor);
    }

    public final WebServiceTemplateConfigurer interceptor(ClientInterceptor interceptor) {
        ClientInterceptor[] interceptors = ArrayUtils.add(template.getInterceptors(), interceptor);
        template.setInterceptors(interceptors);
        return this;
    }

    @Getter
    @Setter
    private static class AuthenticatingUrlConnectionMessageSender extends HttpsUrlConnectionMessageSender {

        private String username;
        private String password;
        private String authorization;

        @Override
        public void afterPropertiesSet() throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append("Basic ");
            sb.append(credential());
            authorization = sb.toString();
            super.afterPropertiesSet();
        }

        private String credential() throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(getUsername());
            sb.append(':');
            sb.append(getPassword());
            return Base64.getEncoder().encodeToString(sb.toString().getBytes("US-ASCII"));
        }

        @Override
        protected void prepareConnection(HttpURLConnection connection) throws IOException {
            super.prepareConnection(connection);
            connection.setRequestProperty("Authorization", authorization);
        }

    }

    private static class DummyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

}
