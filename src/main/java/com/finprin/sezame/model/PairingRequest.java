package com.finprin.sezame.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PairingRequest {

    @JsonProperty("username")
    private String userName;

    public PairingRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
