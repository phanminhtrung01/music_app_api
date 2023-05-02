package com.example.music_app_api.config;

import com.example.music_app_api.component.BusinessService;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Configuration
public class ConfigHttpClient {
    public String response;
    public Boolean hasCookies = false;
    public HttpClientContext clientContext;

    @Autowired
    @Lazy
    public ConfigHttpClient() {
    }

    @Bean
    HttpHost httpHost() {

        return new HttpHost("103.101.90.181", 6446);
    }

    @Bean
    BasicCredentialsProvider basicCredentialsProvider() {

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(
                        getHttpHost().getHostName(),
                        getHttpHost().getPort()),
                new UsernamePasswordCredentials(
                        getUPProxy().get("username"),
                        getUPProxy().get("password").toCharArray()));

        return credentialsProvider;
    }

    private @NotNull BasicCookieStore cookieStore() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookies(BusinessService.cookiesBase.get("base"));

        return cookieStore;
    }

    private HttpHost getHttpHost() {
        return httpHost();
    }

    private BasicCredentialsProvider getBasicCredentialsProvider() {

        return basicCredentialsProvider();
    }

    private CookieStore getBasicCookieStore() {
        return cookieStore();
    }

    private @NotNull @Unmodifiable Map<String, String> getUPProxy() {
        String username = "pmhdv2k1";
        String password = "n163164182183217";
        return Map.of(
                "username", username,
                "password", password
        );
    }

    private @NotNull HttpClientBuilder closeableHttpClient(Boolean hasProxy) {
        PoolingHttpClientConnectionManager connectionManager = new
                PoolingHttpClientConnectionManager();
        Timeout timeoutRequest = Timeout.ofSeconds(10);

        SocketConfig socketConfig = SocketConfig
                .custom()
                .setSoTimeout(timeoutRequest)
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(connectionManager);

        if (hasCookies) {
            httpClientBuilder
                    .setDefaultCookieStore(getBasicCookieStore());
        }

        if (hasProxy) {
            httpClientBuilder
                    .setProxy(getHttpHost())
                    .setDefaultCredentialsProvider(getBasicCredentialsProvider());
        }

        return httpClientBuilder;
    }

    public ConfigHttpClient sendRequest(
            @NotNull RequestMethod requestMethod,
            URI uri, Boolean hasProxy) throws IOException {
        final BasicHttpClientResponseHandler handler = new BasicHttpClientResponseHandler();
        final HttpClientBuilder httpClientBuilder = closeableHttpClient(hasProxy);
        final HttpClientContext context = new HttpClientContext();
        CloseableHttpClient httpClient = httpClientBuilder.build();

        final HttpUriRequestBase httpRequest =
                new HttpUriRequestBase(requestMethod.name(), uri);

        String response = httpClient.execute(httpRequest, context, handler);
        httpClient.close();

        this.setResponse(response);
        this.setClientContext(context);

        return this;
    }

    public ConfigHttpClient setHasCookies(Boolean hasCookies) {
        this.hasCookies = hasCookies;
        return this;
    }

    private void setResponse(String response) {

        this.response = response;
    }

    private void setClientContext(HttpClientContext clientContext) {

        this.clientContext = clientContext;
    }

    public String getResponse() {
        return response;
    }

    public HttpClientContext getClientContext() {

        return clientContext;
    }

}
