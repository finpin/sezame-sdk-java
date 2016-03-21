package com.finpin.sezame;

import com.finpin.sezame.model.CertificateSigningRequest;
import com.finpin.sezame.model.LoginRequest;
import com.finpin.sezame.model.LoginResponse;
import com.finpin.sezame.model.LoginStatusResponse;
import com.finpin.sezame.model.PairingRequest;
import com.finpin.sezame.model.PairingResponse;
import com.finpin.sezame.model.RegistrationResponse;
import com.finpin.sezame.util.CertificateWriter;
import com.finpin.sezame.util.QrCode;
import com.finpin.sezame.util.QrCodePairingData;
import com.finpin.sezame.model.RegistrationRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Scanner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@Ignore
public class SezameRestClientTest {

    private static final Logger log = LoggerFactory.getLogger(SezameRestClientTest.class);

    public static final String PATH_TO_QR_CODE_OUTPUT_IMAGE = "C:/dev/Coding/sezame/qr_code.png";
    public static final String PATH_TO_CLIENT_NEW_CERTIFICATE = "C:/dev/Coding/sezame/certs/client_signed.pem";

    public static final String SAMPLE_CLIENT_JKS_KEYSTORE_FILENAME = "sample_clientkeystore.jks";
    public static final String SAMPLE_CLIENT_JKS_KEYSTORE_PASSWORD = "testpwd";
    public static final String SAMPLE_CLIENT_PKCS12_KEYSTORE_FILENAME = "sample_clientkeystore.p12";
    public static final String SAMPLE_CLIENT_PKCS12_KEYSTORE__PASSWORD = "";

    public static final String SAMPLE_SERVER_KEYSTORE_FILENAME = "sample_serverkeystore.jks";
    public static final String SAMPLE_SERVER_KEYSTORE_PASSWORD = "changeit";

    public static final String TEST_REGISTRATION_EMAIL = "michael@bretterklieber.com";
    public static final String TEST_SERVICE_NAME = "JAVA REST client";
    private static final String CLIENT_SECRET = "502094609989e81857824edf8215ccb86a4392314286cd7d1e052025fbc3ee78";
    private static final String CLIENT_CODE = "56e3065809b5d2.39299328";

    public static final String TEST_USER_NAME = "jonny";
    public static final String TEST_USER_NAME_NOT_EXISTING = "some_user_name_not_existing";
    private static final String TEST_USER_LOGIN_AUTH_ID = "56e44c39e0bda6a7878b45cf";
    private static final String TEST_USER_LOGIN_AUTH_ID2 = "56e46691e0bda6a9878b45c9";
    public static final String SAMPLE_CLIENT_CSR_FILE_NAME = "sample_client.csr";

    private SezameRestClient client;


    @Before
    public void setup() {
        client = new SezameRestClient();
    }

    @Test
	public void shouldRegisterClientApplication() {
        RegistrationResponse response = client.registerClient(new RegistrationRequest(TEST_REGISTRATION_EMAIL, TEST_SERVICE_NAME));
        log.info(response.toString());
	}

    @Test
    public void givenPairedClient_shouldReturnStatusTrue() {
        initiateSslContextForHttpClient();
        PairingRequest pairingRequest = new PairingRequest(TEST_USER_NAME);
        boolean status = client.getPairedClientStatus(pairingRequest);
        assertTrue("Expected status to be paired", status);
    }

    @Test
    public void givenUnpairedClient_shouldReturnStatusFalse() {
        initiateSslContextForHttpClient();
        PairingRequest pairingRequest = new PairingRequest(TEST_USER_NAME_NOT_EXISTING);
        boolean status = client.getPairedClientStatus(pairingRequest);
        assertFalse("Expected status to be unpaired", status);
    }

    @Test
    public void givenUserLoginAuthorized_shouldReturnStatusTrue() {
        initiateSslContextForHttpClient();
        LoginStatusResponse response = client.getLoggedInUserStatus(TEST_USER_LOGIN_AUTH_ID);
        log.info(response.toString());
        assertEquals("Expected status to be logged in", "some", response.getStatus());
    }

    @Test
    public void givenUserNotAuthorized_whenRequestingLogin_shouldReturnStatusInitiated() {
        initiateSslContextForHttpClient();
        LoginRequest loginRequest = new LoginRequest(TEST_USER_NAME);
        loginRequest.setMessage("Call me back @https");
        loginRequest.setTimeout((short) 1440);
        loginRequest.setType("auth");
        loginRequest.setCallbackUrl("https://mockbin.org/bin/08f77946-1af7-4873-9312-9de3e209528b");
        LoginResponse loginResponse = client.loginUser(loginRequest);
        log.info(loginResponse.toString());
    }

    @Test
    public void shouldGetCertificateAndWriteToFile() throws CertificateException, IOException {
        String sampleCsr = getSampleCertificateSigningRequest();
        CertificateSigningRequest certificateSigningRequest = new CertificateSigningRequest(sampleCsr, CLIENT_SECRET);
        Certificate certificate = client.getClientCertificate(certificateSigningRequest);
        log.info(certificate.toString());

        CertificateWriter writer = new CertificateWriter(certificate);
        writer.writeAsPemToFile(PATH_TO_CLIENT_NEW_CERTIFICATE);
    }

    @Test
    public void givenUserNotPaired_whenRequestingPairing_shouldReturnIdAndClientCode() throws Exception {
        initiateSslContextForHttpClient();
        PairingRequest pairingRequest = new PairingRequest(TEST_USER_NAME);
        PairingResponse pairingResponse = client.pairClient(pairingRequest);
        log.info((pairingResponse.toString()));
        assertFalse(pairingResponse.getId().isEmpty());
        assertFalse(pairingResponse.getClientCode().isEmpty());

        QrCodePairingData qrCodeData = new QrCodePairingData(TEST_USER_NAME, pairingResponse);
        QrCode qrCode = new QrCode(qrCodeData);
        qrCode.writeToImageFile(PATH_TO_QR_CODE_OUTPUT_IMAGE);
    }

    @Test
    public void testSslHandshake() throws IOException {
        // Note: If you have trouble establishing a two-way authenticated SSL connection run this test
        //       with JVM options -Djavax.net.debug=ssl and it will spit out everything going on (handshakes etc.)
        SSLContext sslContext = initiateSslContextForHttpClient();
        SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(SezameRestClient.HOST, 443);
        socket.startHandshake();
        assertTrue(socket.isConnected());
    }


    private String getSampleCertificateSigningRequest() {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SAMPLE_CLIENT_CSR_FILE_NAME);
        String certificateSigningRequest = new Scanner(stream).useDelimiter("\\Z").next();
        log.info(certificateSigningRequest);
        return certificateSigningRequest;
    }

    private KeyStore getSampleClientKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SAMPLE_CLIENT_PKCS12_KEYSTORE_FILENAME);
            keyStore.load(stream, SAMPLE_CLIENT_PKCS12_KEYSTORE__PASSWORD.toCharArray());
            return keyStore;
        }
        catch (Exception e) {
            Assert.fail("Could not load client keystore from resources!\nError message: " + e.getMessage());
            return null;
        }
    }

    private KeyStore getSampleServerKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SAMPLE_SERVER_KEYSTORE_FILENAME);
            keyStore.load(stream, SAMPLE_SERVER_KEYSTORE_PASSWORD.toCharArray());
            return keyStore;
        }
        catch (Exception e) {
            Assert.fail("Could not load server keystore from resources!\nError message: " + e.getMessage());
            return null;
        }
    }

    private SSLContext initiateSslContextForHttpClient() {
        try {
            client.setClientKeystore(getSampleClientKeystore(), SAMPLE_CLIENT_PKCS12_KEYSTORE__PASSWORD);
            client.setServerKeystore(getSampleServerKeystore());
            HttpsURLConnection.setDefaultSSLSocketFactory(client.getSslContext().getSocketFactory());
            return client.getSslContext();
        }
        catch (Exception e) {
            log.info(e.toString());
            Assert.fail("An error occurred trying to initiate the SSL context!\nError message: " + e.getMessage());
            return null;
        }
    }
}
