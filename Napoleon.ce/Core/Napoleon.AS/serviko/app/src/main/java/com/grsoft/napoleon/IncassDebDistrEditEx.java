package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.IncassDebDistrEx;
import com.grsoft.dataobjects.impl.IncassImplEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IncassDebDistrEditEx extends IncassDebDistrEdit{
    Spinner spDogovor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spDogovor = findViewById(R.id.spDogovor);

        findViewById(R.id.btnSend).setVisibility(View.GONE);

        List<Firm> data = new ArrayList<>();
        for (Firm d : DbReader.fetch(Firm.class))
            data.add(d);

        Collections.sort(data, new Comparator<Firm>() {

            @Override
            public int compare(Firm lhs, Firm rhs) {
                return lhs.name.compareToIgnoreCase(rhs.name);
            }
        });

        data.add(0, new Firm());
        ArrayAdapter<Firm> filter = new ArrayAdapter<Firm>(this, R.layout.simple_spinner_layout, data);
        spDogovor.setAdapter(filter);

        boolean editable = (((IncassDebDistrEx) doc.getData()).items.size() == 0) && (doc.getData().sum == 0);

        spDogovor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editable)
                    enableControls(position != 0);
                adapter.refreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        if (((IncassDebDistrEx)doc.getData()).dogovor.length() > 0){
            for(int i = 1; i < data.size(); i++){
                Firm d = data.get(i);
                if (d.id.equals(((IncassDebDistrEx)doc.getData()).dogovor)){
                    final int pos = i;
                    spDogovor.post(()->{spDogovor.setSelection(pos, true);});
                    break;
                }
            }
        }

        if (!editable){
            spDogovor.setEnabled(false);
            enableControls(false);
            ((ListView)findViewById(R.id.lvItems)).setOnItemClickListener(null);
        }
    }

    @Override
    protected void setDocument() {
        super.setDocument();

        Firm d = (Firm) spDogovor.getSelectedItem();

        if (d != null)
            ((IncassDebDistrEx)doc.getData()).dogovor = d.id;
    }

    private void enableControls(boolean enabled) {
        for (int id : new int []{R.id.rbCustom, R.id.rbAuto, R.id.tvDate, R.id.edCount, R.id.edRemark, R.id.btnSend})
            findViewById(id).setEnabled(enabled);

        keyHelper.setEnabled(enabled);
    }

    @Override protected int getContentViewID() { return R.layout.incass_deb_distrex; }

    protected ItemsAdapter createAdapter() { return new ItemsAdapter(){
        @Override
        public boolean filter(Item i) {
            if (spDogovor == null)
                spDogovor = findViewById(R.id.spDogovor);

            Firm dogovor = (Firm) spDogovor.getSelectedItem();
            return dogovor != null ? ((DeliveryEx)i.dlv.delivery).dogovor.equals(dogovor.id) : false;
        }
    }; }

    @Override
    public void onBackPressed() {
        if( !Features.OK_BTN_INCASS )
            save();
        else if( doc.isEditable() && doc.sum() == 0 )
            ((IncassImplEx)doc).forceDelete();
        super.onBackPressed();
    }

    protected void handlingInvalidSum(){ ((IncassImplEx)doc).forceDelete(); }
}
