package com.serviko.dataobjects;

import androidx.lifecycle.LiveData;

public class LoginResult extends LiveData<String> {
    public LoginResult() {
        super("");
    }

    public void setResult(String result) {
        setValue(result);
    }
}
