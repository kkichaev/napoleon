package com.grsoft.napoleon;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class IncassEditEx extends IncassDebDistrEdit {
    @Override protected int getContentViewID() { return R.layout.incass_ex; }

    @Override
    protected void childInit(Incass incass, Org org) {
        super.childInit(incass, org);

        OrgImpl oi = new OrgImpl();
        final OrgEx oe = (OrgEx) oi.getData();
        oe.id = doc.getId();
        oi.read();
        oi.close();

        int sel = 0;
        String fe = ((IncassEx)doc.getData()).firmCode;
        List<KeyValue> firms = new ArrayList<>();
        for(Firm f : DbReader.fetch(Firm.class)) {
            if(f.id.equals(fe)) {
                sel = firms.size();
            }
            firms.add(new KeyValue(f.id, f.name));
        }

        Spinner spFirma = (Spinner) findViewById(R.id.spFirm);
        ArrayAdapter<KeyValue> aa = new ArrayAdapter<>(this, R.layout.simple_spinner_layout, firms);
        spFirma.setAdapter(aa);
        if(sel < firms.size())
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
    protected void setDocument() {
        super.setDocument();

        Spinner sp = findViewById(R.id.spFirm);
        KeyValue kv = (KeyValue) sp.getSelectedItem();
        if( kv != null) {
            ((IncassEx) doc.getData()).firmCode = kv.key.toString();
        }
    }

    @Override
    protected String makeDeliveryWhere(Org o) {
        String ret = super.makeDeliveryWhere(o);
        KeyValue od = (KeyValue) ((Spinner)findViewById(R.id.spFirm)).getSelectedItem();
        if(od != null) {
            ret += " and firmCode='" + od.key.toString() + "'";
        } else {
            IncassEx ie = (IncassEx) doc.getData();
            ret += " and firmCode='" + ie.firmCode + "'";
        }
        return ret;
    }
}
