package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.FirmImpl;

public class CreateSalesEx extends CreateSales {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(spFirma != null && salesImpl.isEditable()) {
            spFirma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Firm fe = (Firm) ((FirmImpl)parent.getAdapter().getItem(position)).getData();
                    if(fe != null) {
                        CheckBox cb = (CheckBox)findViewById(R.id.cbTabak);
                        cb.setEnabled(true);
                    }
                }

                @Override public void onNothingSelected(AdapterView<?> parent) { }
            });
        }

        ((CheckBox)findViewById(R.id.cbTabak)).setChecked(((SalesEx)salesImpl.getData()).tabak > 0);
    }

    @Override protected int getSalesLayoutId() { return R.layout.createsalesex; }

    @Override
    protected void postOkDone(Sales sales) {
        int tabak = -1;

        Object sel = spFirma.getSelectedItem();

        if (sel != null) {
            Firm fe = (Firm) ((FirmImpl)sel).getData();
            if (fe != null) {
                tabak = ((CheckBox) findViewById(R.id.cbTabak)).isChecked() ? 1 : 0;
            }
        }

        ((SalesEx)sales).tabak =  tabak;
    }
}
