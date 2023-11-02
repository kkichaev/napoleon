package com.ashberrysoft.leadertask.domains.simplexml;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
@Root(name = "Envelope")
public class VerifyuserResponse {

    @Element(name = "error_code")
    @Path("Body/VerifyUserResponse/VerifyUserResult")
    public int mErrorCode;

    @Element(name = "error_string", required = false)
    @Path("Body/VerifyUserResponse/VerifyUserResult")
    public String mErrorString;

    @Element(name = "end_date", required = false)
    @Path("Body/VerifyUserResponse/VerifyUserResult")
    public String mDate;
}