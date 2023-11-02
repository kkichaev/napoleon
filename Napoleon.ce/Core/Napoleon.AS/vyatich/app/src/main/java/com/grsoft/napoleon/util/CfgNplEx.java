package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {
    @Override
    public void resetToDefault() {
        super.resetToDefault();
        setDetaults();
    }

    public void setDetaults() {
        useUpdatePrice = false;
    }
}
