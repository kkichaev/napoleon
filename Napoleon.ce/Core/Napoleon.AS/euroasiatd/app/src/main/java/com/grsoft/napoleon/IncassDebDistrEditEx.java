package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class IncassDebDistrEditEx extends IncassDebDistrEdit{

    @Override protected int getContentViewID() { return R.layout.incass_deb_distrex; }

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);

        final IncassEx ie = (IncassEx) doc.getData();
        Spinner sp = (Spinner)findViewById(R.id.spDover);
        DialogHelper.loadSpinnerFromDataObject(sp, Dover.class, new DialogHelper.Selected<Dover>() {
            @Override public boolean isSelected(Dover object) { return object.number.equals(ie.dover); }
        }, true);
    }

    @Override
    protected void setDocument() {
        super.setDocument();

        Spinner sp = (Spinner)findViewById(R.id.spDover);
        IncassEx ie = (IncassEx) doc.getData();
        Dover d = (Dover) sp.getSelectedItem();
        if(d != null)
            ie.dover = d.number;
    }

    @Override
    protected boolean save() {
        boolean res = false;
        if(isDataValid())
            res = super.save();

        return res;
    }

    private boolean isDogovorValid() {
        Spinner sp = (Spinner)findViewById(R.id.spDover);
        Dover d = (Dover) sp.getSelectedItem();
        return d != null && d.number.trim().length() > 0;
    }

    @Override
    public void onBackPressed() {
        if (isDataValid())
            super.onBackPressed();
    }

    @Override
    protected void send() {
        if (isDataValid())
            super.send();
    }

    public boolean isDataValid() {
        boolean result = isDogovorValid();

        if (!result)
            Toast.makeText(this, R.string.select_dover, Toast.LENGTH_SHORT).show();

        return result;
    }
}
