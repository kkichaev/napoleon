package com.ashberrysoft.leadertask.xml_handlers;

import java.io.Serializable;

import com.ashberrysoft.leadertask.interfaces.LTServerError;

public class ErrorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private LTServerError mError;
    private String mMessage;

    public static ErrorEntity newInstance(LTServerError serverError) {
        final ErrorEntity entity = new ErrorEntity();
        entity.setError(serverError);

        return entity;
    }

    public static ErrorEntity newInstance(int errorCode) {
        final ErrorEntity entity = new ErrorEntity();
        entity.setErrorCode(errorCode);

        return entity;
    }

    public int getErrorCode() {
        return mError.getCode();
    }

    public void setErrorCode(int errorCode) {
        mError = LTServerError.getError(errorCode);
    }

    public LTServerError getError() {
        return mError;
    }

    public void setError(LTServerError error) {
        mError = error;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}