package com.ashberrysoft.leadertask.data_providers.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.core.Persister;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.ashberrysoft.leadertask.application.Config;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.simplexml.CreateUserEnvelope2;
import com.ashberrysoft.leadertask.service.LeaderTaskService;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AbstractHTTPServiceRequest;

import static com.ashberrysoft.leadertask.interfaces.FileHandlerConstants.SOAP_NAMESPACE;

public class CreateUser2 extends AbstractHTTPServiceRequest<Serializable, Void, R> {

    private static final long serialVersionUID = 1L;
    public static final String METHOD_NAME = "CreateUser2";

    // REGISTRATION ERROR's
    public static final int REGISTRATION_COMPLETE = 0;
    public static final int ERROR_USER_EXIST = 10;
    public static final int ERROR_FORMAT_EMAIL = 11;
    // LOGIN ERROR's
    public static final int ERROR_WRONG_AUTH = 3;
    public static final int ERROR_ACCOUNT_FROZEN = 9;
    public static final int ERROR_END_EMP_LIMIT = 16;
    public static final int ERROR_WRONG_SERVICE_1 = -1;
    public static final int ERROR_WRONG_SERVICE_2 = 22222;
    public static final int ERROR_API_DISABLED = 26;
    
    // VALUE's
    private String mEmail;
    private String mName;
    private String mPass;
    private boolean mCustomReg;
    private int mErrorCode;
    private boolean mSuccesful;
    private CreateUserEnvelope2 mAnswer;
    private String token; 
    private String google_or_facebook;

    public CreateUser2(Context context, String email, String name, String pass, boolean CustomReg, String g_or_f, String session_token) {
        super(context);

        mEmail = email;
        mName = name;
        mPass = pass;
        mCustomReg = CustomReg;
        token = session_token;
        google_or_facebook = g_or_f;
    }

    @Override
    protected Void prepareParameters() throws AbstractDataRequestException {
        return null;
    }

    @Override
    protected R sendRequest(Void p) throws AbstractDataRequestException {
        try {
            // process response
            final InputStream input = OkHttpConnection.PostWithOkHttp(METHOD_NAME, writeSOAPRequest().getBytes("UTF-8"), false);
            if (input == null) {
                throw new NullPointerException();
            }

            final Reader reader = new InputStreamReader(input);

            mAnswer = new Persister().read(CreateUserEnvelope2.class, reader, false);
            input.close();
            reader.close();

            mErrorCode = mAnswer.getErrorCode();
            if (mErrorCode != 0) {
                throw new NullPointerException();
            }
            
            mSuccesful = true;
        } catch (Exception e) {
            mSuccesful = false;
        }

        return null;
    }

    @Override
    protected Serializable parseResult(R data) throws AbstractDataRequestException {
        return null;
    }

    @Override
    public String getResultAction() {
        if (mSuccesful) {
            return ServiceConstants.ACTION_REGISTRATION2;
        } else {
            return ServiceConstants.ACTION_REGISTRATION_NOT_SUCCESSFUL2;
        }
    }

    private String writeSOAPRequest() {
        String SOAP_NAMESPACE = LTSettings.getInstance().getSyncNamespace();
        String XML_SYNC_URL = " xmlns=\""+SOAP_NAMESPACE+"\"";
        final StringBuilder request = new StringBuilder();
        request.append(BaseSOAP.XML_HEADER_1);
        request.append(BaseSOAP.XML_HEADER_2);
        request.append(BaseSOAP.getOpen(BaseSOAP.XML_BODY));
        request.append(BaseSOAP.getOpen(METHOD_NAME + XML_SYNC_URL));

        request.append(BaseSOAP.getValueLine("email", Utils.escapeCharacter(mEmail)));
        request.append(BaseSOAP.getValueLine("username", Utils.escapeCharacter(mName)));
        if(mCustomReg==false)
        {
        	request.append(BaseSOAP.getValueLine("password", Utils.escapeCharacter(mPass)));
        }
        else
        {
        	if(token != null)
        	{
        		request.append(BaseSOAP.getValueLine(google_or_facebook+"_token", token));
        	}
        	mCustomReg = false;
        }
        
        request.append(BaseSOAP.getValueLine("system_name", "android"));
        request.append(BaseSOAP.getValueLine("language", mContext.getString(R.string.currlang)));

        int permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            if (mPhoneNumber == null) {
                mPhoneNumber = "";
            }
            int zoneInt = TimeZone.getDefault().getRawOffset()/60/60/1000;
            String zone = ""+ (zoneInt > 0 ? "+"+zoneInt : ""+zoneInt) ;
            request.append(BaseSOAP.getValueLine("phone", mPhoneNumber+" ("+zone+")"));
        }

        request.append(BaseSOAP.getClose(METHOD_NAME));
        request.append(BaseSOAP.getClose(BaseSOAP.XML_BODY));
        request.append(BaseSOAP.getClose(BaseSOAP.XML_ENVELOPE));

        return request.toString();
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected String getContentType() {
        return "text/xml; charset=utf-8";
    }

    @Override
    protected boolean shouldLogInteractionToLogCat() {
        return false;
    }

    @Override
    protected String getServiceAction() {
        return ServiceConstants.RECIVE;
    }

    @Override
    protected Class<?> getServiceClass() {
        return LeaderTaskService.class;
    }

    public CreateUserEnvelope2 getAnswer() {
        return mSuccesful ? mAnswer : null;
    }

    public Integer getToastMessageId() {
        switch (mErrorCode) {
                
        case REGISTRATION_COMPLETE:
            return R.string.reg_complete;

        case ERROR_USER_EXIST:
            return R.string.error_user_exist;

        case ERROR_FORMAT_EMAIL:
            return R.string.error_format_email;

        case ERROR_WRONG_AUTH:
            return R.string.error_wrong_auth;

        case ERROR_ACCOUNT_FROZEN:
            return R.string.error_account_frozen;
            
        case ERROR_END_EMP_LIMIT:
            return R.string.error_end_emp_limit;

        case ERROR_WRONG_SERVICE_1:
        case ERROR_WRONG_SERVICE_2:
            return R.string.error_wrong_serv;

        case ERROR_API_DISABLED:
            return R.string.error_api_disabled;

        default:
            return null;
        }
    }
}