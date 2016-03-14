package com.finprin.sezame.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateSigningResponse {

    @JsonProperty("cert")
    private String certificat;

    public String getCertificate() {
        return certificat;
    }

    public void setCertificat(String certificat) {
        this.certificat = certificat;
    }
}
