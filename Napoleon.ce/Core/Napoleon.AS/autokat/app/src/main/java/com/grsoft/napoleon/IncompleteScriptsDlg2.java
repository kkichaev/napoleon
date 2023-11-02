package com.grsoft.napoleon;

import android.os.Bundle;

public class IncompleteScriptsDlg2 extends IncompleteScriptDlg{

    public IncompleteScriptsDlg2(){}


    public IncompleteScriptsDlg2(long rowid) {
        super(rowid);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.incompletescript_dialog2;
    }

    @Override
    public boolean getSyncFlag() {
        return false;
    }
}
