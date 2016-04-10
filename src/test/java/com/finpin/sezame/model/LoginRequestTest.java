package com.finpin.sezame.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LoginRequestTest  {

    @Test
    public void givenLoginTypeEnum_whenJsonSerializing_shouldReturnStringValue() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String loginTypeAuthenticateAsJson = mapper.writeValueAsString(LoginRequest.LoginType.AUTHENTICATE);
        assertEquals("\"auth\"", loginTypeAuthenticateAsJson);

        String loginTypeFraudAsJson = mapper.writeValueAsString(LoginRequest.LoginType.FRAUD);
        assertEquals("\"fraud\"", loginTypeFraudAsJson);
    }

}