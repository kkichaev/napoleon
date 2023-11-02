package com.ashberrysoft.leadertask.data_providers.network;

import java.io.Serializable;

import android.content.Context;

import com.ashberrysoft.leadertask.R;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Класс для ошибки от сервера
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik <vadim.welldone@gmail.com>
 */

public class LeaderTaskException extends AbstractDataRequestException implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final int ERROR_NO_SPACE_ON_DEVICE = -1001;
    public static final int ERROR_WRONG_AUTH = 3;
    public static final int ERROR_ACCOUNT_EXPIRED = 6;
    public static final int ERROR_ACCOUNT_FROZEN = 9;
    public static final int ERROR_STANDARD_VERSION_DURING_SYNCHRONIZATION = 15;
    public static final int ERROR_END_EMP_LIMIT = 16;
    public static final int ERROR_WRONG_SERVER = -1;
    public static final int ERROR_INTERNET_ACCESS = 11111;
    public static final int ERROR_API_DISABLED = 26;
    public static final int ERROR_SESSION_NOT_CREATED = 25;
    public static final int ERROR_ACCOUNT_BLOCKED = 14;

    public enum ErrorType {
        error_serv, //
        SQLITE_ERROR, //
        XML_PARSE_ERROR, //
        UNKNOWN_TYPE_ERROR, //
        SSL_HANDSHAKE_ERROR, //
        FILE_NOT_DOWNLOADED
    }

    private ErrorType mType;
    private Context mContext;
    private int mCode;
    private String mMessage;// string message from XmlPullParserException

    public LeaderTaskException(ErrorType type, Context context, Object code_or_message, Throwable e) {
        super(e);
        setType(type);
        /*
         * if we received Integer instance then this is code from regular Exception, else if we received String instance
         * then this is message from XmlPullParserException
         */
        mContext = context;

        if (code_or_message instanceof Integer) {
            setCode((Integer) code_or_message);
        } else if (code_or_message instanceof String) {
            setMessage((String) code_or_message);
        }
    }

    public ErrorType getType() {
        return mType;
    }

    public void setType(ErrorType mType) {
        this.mType = mType;
    }

    @Override
    public String toString() {
        switch (getType()) {
        // server error
        case error_serv:
            switch (getCode()) {
            case ERROR_WRONG_AUTH:
                return mContext.getString(R.string.error_wrong_auth);

            case ERROR_ACCOUNT_EXPIRED:
                return mContext.getString(R.string.error_account_expired);

            case ERROR_ACCOUNT_FROZEN:
                return mContext.getString(R.string.error_account_frozen);

            case ERROR_STANDARD_VERSION_DURING_SYNCHRONIZATION:
                return mContext.getString(R.string.error_standard_version_during_synchronization);

            case ERROR_END_EMP_LIMIT:
                return mContext.getString(R.string.error_end_emp_limit);

            case ERROR_WRONG_SERVER:
                return mContext.getString(R.string.error_wrong_serv);

            case ERROR_INTERNET_ACCESS:
                return mContext.getString(R.string.error_internet_access);

            case ERROR_API_DISABLED:
                return mContext.getString(R.string.error_api_disabled);

            case ERROR_ACCOUNT_BLOCKED:
                return mContext.getString(R.string.error_account_blocked);

            default:
                return mContext.getString(R.string.error_server) + " "
                        + (getCode() == 0 ? getMessage() : String.valueOf(getCode()));
            }

        // XmlPullParser error
        case XML_PARSE_ERROR:
            switch (getCode()) {
                case ERROR_NO_SPACE_ON_DEVICE:
                    return mContext.getString(R.string.no_space_left_on_device);

                default:
                    return mContext.getString(R.string.error_xml_pull_parser, new Object[] { getMessage() });
            }

        // SSL handshake exception
        case SSL_HANDSHAKE_ERROR:
            return mContext.getString(R.string.error_ssl_handshake);

        case FILE_NOT_DOWNLOADED:
            return mContext.getString(R.string.error_file_not_downloaded);

        default:
            return super.toString();
        }
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int mCode) {
        this.mCode = mCode;
    }

    // get string message from XmlPullParserException
    public String getMessage() {
        return mMessage;
    }

    // set string message from XmlPullParserException
    public void setMessage(String message) {
        mMessage = message;
    }
}