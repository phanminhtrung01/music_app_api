package com.example.music_app_api.config;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Configuration
public class ConfigHttpClient {

    @Bean
    HttpHost httpHost() {
        return new HttpHost("192.168.43.1", 10809);
    }

    private HttpHost getHttpHost() {
        return httpHost();
    }

    private @NotNull HttpClientBuilder closeableHttpClient(
            @NotNull List<Cookie> cookies) {

        PoolingHttpClientConnectionManager connectionManager = new
                PoolingHttpClientConnectionManager();
        Timeout timeoutRequest = Timeout.ofSeconds(10);

        SocketConfig socketConfig = SocketConfig
                .custom()
                .setSoTimeout(timeoutRequest)
                .build();
        connectionManager.setDefaultSocketConfig(socketConfig);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();

        BasicCookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookies(cookies.toArray(new Cookie[0]));

        return httpClientBuilder
                .setConnectionManager(connectionManager)
                .setDefaultCookieStore(cookieStore);
    }

    public final @NotNull @Unmodifiable Map<String, Object> sendRequest(
            @NotNull RequestMethod requestMethod,
            URI uri, List<Cookie> cookies) throws IOException {
        final BasicHttpClientResponseHandler handler = new BasicHttpClientResponseHandler();
        final HttpClientBuilder httpClientBuilder = closeableHttpClient(cookies);
        final HttpClientContext context = new HttpClientContext();
        CloseableHttpClient httpClient = httpClientBuilder.build();

        final HttpUriRequestBase httpRequest =
                new HttpUriRequestBase(requestMethod.name(), uri);

        String response = httpClient.execute(httpRequest, context, handler);
        httpClient.close();

        return Map.of(
                "response", response,
                "context", context
        );

    }
}
