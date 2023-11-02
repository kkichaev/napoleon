package com.grsoft.napoleon;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

public class IncassEditEx extends IncassDebDistrEdit {
    Spinner spDog;
    Org org;

    @Override protected int getContentViewID() { return R.layout.incassex; }

    @Override
    protected void childInit(final Incass incass, Org org) {
        super.childInit(incass, org);

        this.org = org;

        spDog = findViewById(R.id.spDogovor);
        DialogHelper.loadSpinnerFromDataObject(spDog, OrgDogovor.class, new DialogHelper.Selected<OrgDogovor>() {
            @Override public boolean isSelected(OrgDogovor object) { return ((IncassEx)incass).dogovor.equals(object.id); }
        }, false, "name",
                "ido='" + ((OrgEx)org).ido + "'");

        spDog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDeliveries(IncassEditEx.this.org);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        spDog.setEnabled(doc.isEditable());
    }

    @Override
    public void postSendExecute(boolean result) {
        super.postSendExecute(result);
        spDog.setEnabled(doc.isEditable());
    }

    @Override
    protected String makeDeliveryWhere(Org o) {
        String ret = super.makeDeliveryWhere(o);
        OrgDogovor od = (OrgDogovor) spDog.getSelectedItem();
        if(od != null) {
            ret += " and dogovor='" + od.id + "'";
        }
        return ret;
    }

    @Override
    protected void setDocument() {
        super.setDocument();

        OrgDogovor od = (OrgDogovor) spDog.getSelectedItem();
        if(od != null)
            ((IncassEx)doc.getData()).dogovor = od.id;
    }
}
