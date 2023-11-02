package com.grsoft.napoleon.util;

public class CfgNplEx extends CfgNpl {
    public String scannerAddress = "";
    public String scannerName = "";
    public String uriGood = "";
    public String uriFail = "";

    @Override
    public void resetToDefault() {
        super.resetToDefault();
        useUpdatePrice = false;

    }
}
