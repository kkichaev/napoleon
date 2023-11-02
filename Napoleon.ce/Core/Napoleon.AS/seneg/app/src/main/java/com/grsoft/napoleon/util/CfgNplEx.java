package com.grsoft.napoleon.util;

public class CfgNplEx extends  CfgNpl {

    public CfgNplEx() {
        resetToDefault();
    }

    @Override
    public void resetToDefault() {
        super.resetToDefault();
        address = "mobile.seneg.ru";
        port = 41551;
    }
}
