/*
* (c)Copyright 2013-2014 Ken Yee, KEY Enterprise Solutions 
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.msp.findMyBeer.android_ddp_client;

import com.msp.findMyBeer.java_ddp_client.DDPClient;
import com.msp.findMyBeer.java_ddp_client.DDPListener;
import com.msp.findMyBeer.java_ddp_client.EmailAuth;
import com.msp.findMyBeer.java_ddp_client.TokenAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/*
 *  Commands for Meteor Server
 *
 *  @author Florian Fincke
 */
public abstract class MeteorCommands {

    abstract DDPClient getDDP();
    abstract void handleLoginResult(Map<String, Object> jsonFields);

    /**
     * Logs in using resume token
     * @param token resume token
     */
    public void login(String token) {
        TokenAuth tokenAuth = new TokenAuth(token);
        Object[] methodArgs = new Object[1];
        methodArgs[0] = tokenAuth;
        getDDP().call("login", methodArgs,  new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
    }

    /**
     * Logs in using username/password
     * @param username username/email
     * @param password password
     */
    public void login(String username, String password) {
        Object[] methodArgs = new Object[1];
        EmailAuth emailpass = new EmailAuth(username, password);
        methodArgs[0] = emailpass;
        getDDP().call("login", methodArgs, new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
    }

    /**
     * Register's user into Meteor's accounts-password system
     * @param username user's username (this or email has to be specified)
     * @param email user's email (this or username has to be specified)
     * @param password password
     * @return true if create user called
     */
    public boolean registerUser(String username, String email,
                                String password) {
        if (((username == null) && (email == null)) || (password == null)) {
            return false;
        }
        Object[] methodArgs = new Object[1];
        Map<String,Object> options = new HashMap<String,Object>();
        methodArgs[0] = options;
        if (username != null) {
            options.put("username", username);
        }
        if (email != null) {
            options.put("email", email);
        }
        options.put("password", password);
        getDDP().call("createUser", methodArgs,  new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
        return true;
    }

    /**
     * Sends password reset mail to specified email address
     * @param email email address of user
     */
    public void forgotPassword(String email)
    {
        if (email == null) {
            return;
        }
        Object[] methodArgs = new Object[1];
        Map<String,Object> options = new HashMap<String,Object>();
        methodArgs[0] = options;
        options.put("email", email);
        getDDP().call("forgotPassword", methodArgs,  new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
    }

    public boolean findMyBeer(String lngw, String latn, String lnge, String lats) {

        String object = lngw +", "+latn+ ", "+lnge+", "+lats;

        getDDP().call("findMyBeer", object, new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });

        return true;
    }
    public void wasHere(String beerSpotId) {
        getDDP().call("wasHere", "\"" + beerSpotId + "\"", new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
    }
    public boolean addFacebookToUser(JSONObject object) {
        getDDP().call("addFacebookToUser", object.toString(), new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });

        return true;
    }
    /**
     * @param object = accessToken und expiresAt
     * */
    public boolean updateFacebookToken(JSONObject object) {
        getDDP().call("updateFacebookToken", object.toString(), new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });

        return true;
    }
    public boolean submitStationWithTimes(JSONObject object) {
        try {
            object.put("submitType", "time");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getDDP().call("submitStation", object.toString(), new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });

        return true;
    }

    public boolean submitStationWithImage(JSONObject object) {
        try {
            object.put("submitType", "image");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getDDP().call("submitStation", object.toString(), new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });

        return true;
    }

    public boolean comment(JSONObject object) {
        getDDP().call("comment", object.toString(), new DDPListener() {
            @Override
            public void onResult(Map<String, Object> jsonFields) {
                handleLoginResult(jsonFields);
            }
        });
        return true;
    }
}
