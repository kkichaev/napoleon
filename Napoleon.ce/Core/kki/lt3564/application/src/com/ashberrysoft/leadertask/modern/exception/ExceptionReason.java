package com.ashberrysoft.leadertask.modern.exception;

import com.ashberrysoft.leadertask.R;

public enum ExceptionReason {
    NULL(R.string.empty_string),
    UNKNOWN(R.string.exception_unknown), //
    SSL(R.string.exception_ssl), //
    SQLITE(R.string.exception_sqlite), //
    SERVER(R.string.exception_server), //
    PARSING_REQUEST(R.string.exception_parsing_request), //
    PARSING_RESPONSE(R.string.exception_parsing_response), //
    PARSING_OVERALL(R.string.exception_parsing_overall), //
    RESET_LINKS(R.string.exception_reset_links), //
    FILE_DOWNLOADING(R.string.exception_file_dowloading), //
    TEST(R.string.exception_test),
    API_DISABLED(R.string.error_api_disabled),
    ACCOUNT_BLOCKED(R.string.error_account_blocked);

    final int mErrorResId;

    ExceptionReason(int errorResId) {
        mErrorResId = errorResId;
    }

    public int getErrorResId() {
        return mErrorResId;
    }
}