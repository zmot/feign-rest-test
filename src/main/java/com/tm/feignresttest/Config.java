package com.tm.feignresttest;


import com.tm.feignresttest.client.ExternalNameServiceClient;
import feign.Client;
import feign.Feign;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class Config {

    @Bean
    @Profile("!https")
    public ExternalNameServiceClient nameServiceClient() {
        return Feign.builder().errorDecoder(new TeapotErrorDecoder())
                .target(ExternalNameServiceClient.class, "http://localhost:8085/external-service/api");
    }

    private static class TeapotErrorDecoder extends ErrorDecoder.Default {
        @Override
        public Exception decode(String methodKey, Response response) {
            return response.status() == HttpStatus.I_AM_A_TEAPOT
                    .value() ? new RetryableException("I'M A TEAPOT!", null) : super.decode(methodKey, response);
        }
    }

    @Bean
    @Profile("https")
    public ExternalNameServiceClient secureNameServiceClient() throws KeyManagementException, NoSuchAlgorithmException {
        return Feign.builder()
                .client(naiveClient())
                .target(ExternalNameServiceClient.class, "https://localhost:8443/external-service/api");
    }

    @Bean
    public Client naiveClient() throws KeyManagementException, NoSuchAlgorithmException {
        return new Client.Default(new NaiveSSLSocketFactory(), new AllowLocalhostHostnameVerifier());
    }

    private static class NaiveSSLSocketFactory extends SSLSocketFactory {
        private final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        private final SSLContext sslContext;

        NaiveSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new NoOpTrustManager()}, null);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return sslSocketFactory.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return sslSocketFactory.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return (isLocalhost(host)) ?
                    sslContext.getSocketFactory().createSocket(socket, host, port, autoClose) :
                    sslSocketFactory.createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            return (isLocalhost(host)) ?
                    sslContext.getSocketFactory().createSocket(host, port) :
                    sslSocketFactory.createSocket(host, port);
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
            return (isLocalhost(host)) ?
                    sslContext.getSocketFactory().createSocket(host, port, localAddress, localPort) :
                    sslSocketFactory.createSocket(host, port, localAddress, localPort);
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return (isLocalhost(host.getHostName())) ?
                    sslContext.getSocketFactory().createSocket(host, port) :
                    sslSocketFactory.createSocket(host, port);
        }

        @Override
        public Socket createSocket(InetAddress host, int port, InetAddress localHost, int localPort) throws IOException {
            return isLocalhost(host.getHostName()) ?
                    sslContext.getSocketFactory().createSocket(host, port, localHost, localPort) :
                    sslSocketFactory.createSocket(host, port, localHost, localPort);
        }

        private boolean isLocalhost(String hostName) {
            return "localhost".equals(hostName);
        }
    }

    private static class NoOpTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class AllowLocalhostHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String host, SSLSession sslSession) {
            return host.equals("localhost");
        }
    }
}
