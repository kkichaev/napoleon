package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;

import java.util.ArrayList;
import java.util.List;

public class IncassEditEx extends IncassDebDistrEdit {

    @Override protected int getContentViewID() { return R.layout.incasseditex; }

    @Override
    protected void init(Bundle bundle) {
        super.init(bundle);

        IncassEx ie = (IncassEx) doc.getData();

        OrgImpl oi = new OrgImpl();
        final OrgEx oe = (OrgEx) oi.getData();
        oe.id = doc.getId();
        oi.read();
        oi.close();

        int sel = 0;
        List<OrgDogovor> dogovors = new ArrayList<>();
        for(OrgDogovor od : oe.dogovors) {
            if(od.id.equals(ie.dogovor)) {
                sel = dogovors.size();
            }
            dogovors.add(od);
        }
        Spinner spFirma = (Spinner) findViewById(R.id.spDogovor);
        ArrayAdapter<OrgDogovor> aa = new ArrayAdapter<OrgDogovor>(this, R.layout.simple_spinner_layout, dogovors);
        spFirma.setAdapter(aa);
        if(sel < dogovors.size())
            spFirma.setSelection(sel);

        inited = false;
        spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDeliveries(oe);
                adapter.refreshData();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    protected String makeDeliveryWhere(Org o) {
        String ret = super.makeDeliveryWhere(o);
        OrgDogovor od = (OrgDogovor) ((Spinner)findViewById(R.id.spDogovor)).getSelectedItem();
        if(od != null) {
            ret += " and firma='" + od.idOrg + "'";
        } else {
            IncassEx ie = (IncassEx) doc.getData();
            ret += " and firma='" + od.idOrg + "'";
        }
        return ret;
    }

    @Override
    protected void setDocument() {
        super.setDocument();

        OrgDogovor od = (OrgDogovor) ((Spinner)findViewById(R.id.spDogovor)).getSelectedItem();
        if(od != null) {
            IncassEx id = (IncassEx) doc.getData();
            id.dogovor = od.id;
        }
    }
}
