package com.grsoft.dataobjects;

public class DocHandleResult extends DataObject {
    public static final int STATUS_OK = 1;
    public static final int STATUS_FAIL = 0;

    public int status = 0;
    public String message = "";


    public boolean isFail() { return status == STATUS_FAIL; }
}
