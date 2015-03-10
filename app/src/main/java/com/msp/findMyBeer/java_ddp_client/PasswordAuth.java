package com.msp.findMyBeer.java_ddp_client;

import java.util.HashMap;


public class PasswordAuth {
    String password;
    HashMap<String,String> user = new HashMap<String,String>();

    PasswordAuth(String pw) {
        this.password = pw;
    }
}
