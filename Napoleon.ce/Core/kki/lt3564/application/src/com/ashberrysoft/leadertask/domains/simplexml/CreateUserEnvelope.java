package com.ashberrysoft.leadertask.domains.simplexml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

@Root(name = "Envelope")
public class CreateUserEnvelope implements Serializable {

    private static final long serialVersionUID = 1L;
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Element(name = "error_code")
    @Path("Body/CreateUserResponse/CreateUserResult")
    private int mErrorCode;

    @Element(name = "error_string", required = false)
    @Path("Body/CreateUserResponse/CreateUserResult")
    private String mErrorString;

    @Element(name = "login", required = false)
    @Path("Body/CreateUserResponse/CreateUserResult")
    private String mLogin;

    @Element(name = "password", required = false)
    @Path("Body/CreateUserResponse/CreateUserResult")
    private String mPassword;

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorString() {
        return mErrorString;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }
}