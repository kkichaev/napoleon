package com.grsoft.network.exception;

import androidx.annotation.Nullable;

public class UploadException extends Exception {

    String message;
    public  UploadException(String message) {
        this.message = message;
    }

    @Nullable
    @Override
    public String getMessage() { return message; }
}
