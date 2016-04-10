package com.finpin.sezame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginRequest {

    public enum LoginType {
        AUTHENTICATE("auth"),
        FRAUD("fraud");

        private final String type;

        LoginType(final String type) {
            this.type = type;
        }

        @JsonValue
        @Override
        public String toString() {
            return type;
        }
    }

    @JsonProperty("username")
    private String userName;

    private String message;
    private LoginType type;

    @JsonProperty("callback")
    private String callbackUrl;

    private short timeout;
    // private String params;

    public LoginRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public short getTimeout() {
        return timeout;
    }

    public void setTimeout(short timeout) {
        this.timeout = timeout;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
