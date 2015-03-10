package com.msp.findMyBeer.java_ddp_client;

public class TokenAuth {
    String resume;

    public TokenAuth(String token) {
        assert(token != null);
        this.resume = token;
    }
}