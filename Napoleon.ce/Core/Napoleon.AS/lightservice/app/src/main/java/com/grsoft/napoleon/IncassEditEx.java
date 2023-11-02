package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Dogovor;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncassEditEx extends IncassEdit {
    @Override protected int getContentViewID() { return R.layout.incassex; }

    void loadFirms(ConfigImpl config, final IncassEx o, final OrgEx org) {
        Config c = config.getData();
        c.key = "Организации";
        config.read();

        final Set<String> availDF = new HashSet<>();
        for(Dogovor d : org.dogovors) {
            availDF.add(d.firm);
        }
        List<KeyValue> firms = new ArrayList<>();
        int sel = DialogHelper.makeListWithKeyFilter(c.value, firms, o.firmCode, new DialogHelper.Filter() {
            @Override public boolean contains(KeyValue value) { return availDF.contains(value.key.toString()); }
        });

        final ArrayAdapter<KeyValue> akv = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, firms);
        Spinner spFirm = findViewById(R.id.spFirm);
        spFirm.setAdapter(akv);
        spFirm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KeyValue selF = akv.getItem(position);
                List<Dogovor> dogs = new ArrayList<>();

                int seld = -1;
                for(Dogovor d : org.dogovors) {
                    if(!d.firm.equals(selF.key.toString())) continue;
                    if(d.id.equals(o.dogovor)) seld = dogs.size();
                    dogs.add(d);
                }

                Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
                ArrayAdapter<Dogovor> aa = new ArrayAdapter<Dogovor>(IncassEditEx.this, R.layout.simple_spinner_layout, dogs);
                aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
                spDog.setAdapter(aa);
                if(seld >= 0) spDog.setSelection(seld);
                else if(aa.getCount() > 0) spDog.setSelection(0);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        if( sel >= 0)
            spFirm.setSelection(sel);
    }

    @Override
    protected void childInit(Incass incass, Org org) {
        ConfigImpl config = new ConfigImpl();
        loadFirms(config, (IncassEx)incass, (OrgEx)org);
        config.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setDocument() {
        super.setDocument();

        IncassEx o = (IncassEx) doc.getData();
        Spinner spDog = (Spinner) findViewById(R.id.spDogovor);
        Dogovor selDog = (Dogovor) spDog.getSelectedItem();
        if(selDog != null) {
            o.dogovor = selDog.id;
            o.firmCode = selDog.firm;
        }
    }
}
