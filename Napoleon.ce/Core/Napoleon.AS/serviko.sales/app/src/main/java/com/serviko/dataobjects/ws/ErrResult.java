package com.serviko.dataobjects.ws;

import com.serviko.dataobjects.xml.WSDLElement;

public class ErrResult {
    public static final int ANSWER_MODE = 0;
    public static final int EXCEPTION_MODE = 1;
    public static final String OLD_VERSION = "-2";

    @WSDLElement(name="ќписаниеќшибки")
    public String error = "";

    @WSDLElement(name="–езультат")
    public boolean result = false;

    public int mode = 0;
}
