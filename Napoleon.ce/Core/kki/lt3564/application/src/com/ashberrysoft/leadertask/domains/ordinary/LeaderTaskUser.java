package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.v2soft.AndLib.dao.AbstractProfile;

public class LeaderTaskUser extends AbstractProfile<String> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String PASSWORD = "PASSWORD";
    private transient SharedPreferences mPreferences;
    private static final String USERNAME = "USERNAME";
    private String mPassword;

    public LeaderTaskUser(SharedPreferences preferences) {
        mPreferences = preferences;
        setName(preferences.getString(USERNAME, ""));
        setPassword(preferences.getString(PASSWORD, ""));
    }

    public LeaderTaskUser(String name, String password) {
        mName = name;
        mPassword = password;
    }

    @Override
    public synchronized void invalidateProfile() {
        setName("");
        setPassword("");
        save();
        super.invalidateProfile();
    }

    public void setNamePassword(String name, String password) {
        setName(name.trim().toLowerCase());
        setPassword(password);
    }

    public void save() {
        if (mPreferences != null) {
            final Editor editor = mPreferences.edit();
            editor.putString(USERNAME, mName);
            editor.putString(PASSWORD, mPassword);
            editor.apply();
        }
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    @Override
    public synchronized boolean isValid() {
        return !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mPassword);
    }
}
