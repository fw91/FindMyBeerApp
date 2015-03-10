package com.msp.findMyBeer.java_ddp_client;

public class UsernameAuth extends PasswordAuth
{
    public UsernameAuth(String username, String pw) {
        super(pw);
        assert(username != null);
        this.user.put("username", username);
    }
}
