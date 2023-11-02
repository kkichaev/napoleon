package com.ashberrysoft.leadertask.interfaces;

import com.ashberrysoft.leadertask.R;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public enum LTServerError {

    NO_ERROR(0, R.string.empty_string), //
    UNKNOWN(-100, R.string.error_server), //
    WRONG_AUTH(3, R.string.error_wrong_auth), //
    ACCOUNT_EXPIRED(6, R.string.error_account_expired), //
    ACCOUNT_FROZEN(9, R.string.error_account_frozen), //
    STANDARD_VERSION_DURING_SYNCHRONIZATION(15, R.string.error_standard_version_during_synchronization), //
    END_EMP_LIMIT(16, R.string.error_end_emp_limit), //
    WRONG_SERV_1(-1, R.string.error_wrong_serv), //
    INTERNET_ACCESS(11111, R.string.error_internet_access),
    NEED_CONFIRM_REGISTRATION(24, R.string.error_need_confirm_registration),
    WRONG_SERV_503(503, R.string.error_timeout),
    WRONG_SERV_504(504, R.string.error_timeout),
    API_DISABLED(26, R.string.error_api_disabled),
    NO_SPACE_ON_DEVISE(-1001, R.string.no_space_left_on_device),
    ACCOUNT_BLOCKED(14, R.string.error_account_blocked);
    
    final int mCode;
    final int mResId;

    LTServerError(int code, int resId) {
        mCode = code;
        mResId = resId;
    }

    public int getCode() {
        return mCode;
    }

    public int getResId() {
        return mResId;
    }

    public static LTServerError getError(int code) {
        for (LTServerError error : values()) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return UNKNOWN;
    }

    public static int getErrorMessageResId(int code) {
        return getError(code).getResId();
    }
}