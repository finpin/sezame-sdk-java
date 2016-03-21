package com.finpin.sezame;

import com.finpin.sezame.model.LoginStatusResponse;
import com.finpin.sezame.model.PairingRequest;
import com.finpin.sezame.model.PairingResponse;
import com.finpin.sezame.model.RegistrationResponse;
import com.finpin.sezame.model.CertificateSigningRequest;
import com.finpin.sezame.model.CertificateSigningResponse;
import com.finpin.sezame.model.LoginRequest;
import com.finpin.sezame.model.LoginResponse;
import com.finpin.sezame.model.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

public class SezameRestClient {

    private static final Logger log = LoggerFactory.getLogger(SezameRestClient.class);
    public static final String HOST = "hqfrontend-finprin.finprin.com";
    private static final String BASE_URL = "https://" + HOST;

    private RestTemplate restTemplate;

    private SSLContext sslContext;
    // manages client certificates
    private KeyManagerFactory clientKeyManager;
    // manages server/root certificates
    private TrustManagerFactory trustManagerFactory;


    public SezameRestClient() {
        restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
        list.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(list);
    }

    public RegistrationResponse registerClient(RegistrationRequest request) {
        RegistrationResponse response = restTemplate.postForObject(BASE_URL + "/client/register", request, RegistrationResponse.class);
        log.debug(response.toString());
        return response;
    }

    public CertificateSigningResponse signClientCertificateRequest(CertificateSigningRequest request) {
        CertificateSigningResponse response = restTemplate.postForObject(BASE_URL + "/client/sign", request, CertificateSigningResponse.class);
        log.debug((response.toString()));
        return response;
    }

    public Certificate getClientCertificate(CertificateSigningRequest request) throws CertificateException {
        CertificateSigningResponse response = signClientCertificateRequest(request);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate certificate = cf.generateCertificate(new ByteArrayInputStream(response.getCertificate().getBytes()));
        log.debug(certificate.toString());
        return certificate;
    }

    public PairingResponse pairClient(PairingRequest request) {
        PairingResponse response = restTemplate.postForObject(BASE_URL + "/client/link", request, PairingResponse.class);
        log.debug((response.toString()));
        return response;
    }

    public boolean getPairedClientStatus(PairingRequest request) {
        boolean status = restTemplate.postForObject(BASE_URL + "/client/link/status", request, boolean.class);
        log.debug("pairing status: " + status);
        return status;
    }

    public LoginResponse loginUser(LoginRequest request) {
        LoginResponse loginResponse = restTemplate.postForObject(BASE_URL + "/auth/login", request, LoginResponse.class);
        log.debug(loginResponse.toString());
        return loginResponse;
    }

    public LoginStatusResponse getLoggedInUserStatus(String loginId) {
        String urlLoginStatus = BASE_URL + "/auth/status/" + loginId;
        LoginStatusResponse response = restTemplate.getForObject(urlLoginStatus, LoginStatusResponse.class);
        log.debug(response.toString());
        return response;
    }

    public void setClientKeystore(KeyStore keyStore, String keyStorePassword)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {

        clientKeyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        clientKeyManager.init(keyStore, keyStorePassword.toCharArray());
    }

    public void setClientKeystore(String keyStorePath, String keyStorePassword, String keyStoreType)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {

        // TODO: validate path
        // load keystore from file; key store types "JKS", "PKCS12", ...
        KeyStore clientKeyStore = KeyStore.getInstance(keyStoreType);
        clientKeyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
        setClientKeystore(clientKeyStore, keyStorePassword);
    }


    public void setServerKeystore(KeyStore keyStore)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {

        // initialize a new TMF with the key store containing trusted server/root certificates
        trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
    }

    public void setServerKeystore(String keystorePath, String keystorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {

        // TODO: validate path
        KeyStore serverPublicKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
        serverPublicKeystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
        setServerKeystore(serverPublicKeystore);
    }

    public SSLContext getSslContext()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        // TODO: Custom exception?
        if (sslContext == null) {
            createSslContextWithClientCertificate();
        }
        return sslContext;
    }


    private void createSslContextWithClientCertificate()
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        // TODO: Assert client key store is present

        sslContext = SSLContext.getInstance("TLS");
        if (trustManagerFactory != null) {
            sslContext.init(clientKeyManager.getKeyManagers(), trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
        }
        else {
            // use default trustManagerFactory, usually falling back to "jre/lib/security/cacerts" file distributed with JRE
            sslContext.init(clientKeyManager.getKeyManagers(), null, new java.security.SecureRandom());
        }
    }
}
