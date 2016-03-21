package com.finpin.sezame.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertificateSigningRequest {

    @JsonProperty("csr")
    private String certificateSigningRequest;

    @JsonProperty("sharedsecret")
    private String sharedSecret;

    public CertificateSigningRequest(String certificateSigningRequest, String sharedSecret) {
        this.certificateSigningRequest = certificateSigningRequest;
        this.sharedSecret = sharedSecret;
    }

    public String getCertificateSigningRequest() {
        return certificateSigningRequest;
    }

    public void setCertificateSigningRequest(String certificateSigningRequest) {
        this.certificateSigningRequest = certificateSigningRequest;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }
}
