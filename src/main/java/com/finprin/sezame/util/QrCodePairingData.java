package com.finprin.sezame.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.finprin.sezame.model.PairingResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QrCodePairingData extends PairingResponse {

    @JsonProperty("username")
    private String userName;

    public QrCodePairingData(String userName, String id, String clientCode) {
        setUserName(userName);
        setId(id);
        setClientCode(clientCode);
    }

    public QrCodePairingData(String userName, PairingResponse pairingResponse) {
        setUserName(userName);
        setId(pairingResponse.getId());
        setClientCode(pairingResponse.getClientCode());
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
