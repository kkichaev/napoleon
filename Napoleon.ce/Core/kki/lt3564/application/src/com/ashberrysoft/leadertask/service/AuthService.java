package com.ashberrysoft.leadertask.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Authentication service..
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */

public class AuthService extends Service {
    public static final String ACCOUNT_TYPE="com.ashberrysoft.leadertask.service.AuthService";
    // Instance field that stores the authenticator object
    private AccountAuthenticator mAuthenticator;
    
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AccountAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
