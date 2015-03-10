package com.msp.findMyBeer.java_ddp_client;

import com.msp.findMyBeer.java_ddp_client.PasswordAuth;


public class EmailAuth extends PasswordAuth
{
    public EmailAuth(String email, String pw) {
        super(pw);
        assert(email != null);
        this.user.put("email", email);
    }
}
