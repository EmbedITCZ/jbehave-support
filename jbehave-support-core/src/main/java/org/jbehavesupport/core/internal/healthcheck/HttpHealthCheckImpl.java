package org.jbehavesupport.core.internal.healthcheck;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.jbehavesupport.core.healthcheck.HealthCheck;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class HttpHealthCheckImpl implements HealthCheck {
    private String url;
    private String username;
    private String password;

    public HttpHealthCheckImpl(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void check() {
        var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
            new AuthScope(new AuthScope(null, -1)),
            new UsernamePasswordCredentials(username, password.toCharArray()));
        try {
            var sslsf = new SSLConnectionSocketFactory(
                new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build());

            var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslsf)
                .build();

            CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setConnectionManager(connectionManager)
                .build();

            HttpGet httpGet = new HttpGet(url);
            httpclient.execute(httpGet, response -> {
                if (response.getCode() != 200) {
                    throw new IllegalStateException(
                        "Status code is " + response.getCode() + ". " + response.getReasonPhrase());
                }
                return null;
            });

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new IllegalStateException(e);
        }
    }
}
