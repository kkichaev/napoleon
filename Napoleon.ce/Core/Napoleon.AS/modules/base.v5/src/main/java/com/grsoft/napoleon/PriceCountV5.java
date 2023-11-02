package com.grsoft.napoleon;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItemV5;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceUnit;
import com.grsoft.dataobjects.PriceV5;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PriceCountV5 extends PriceCount {
    List<PriceUnit> units = null;
    PriceUnit selected = null;

    @Override protected int getContentViewId() {return R.layout.pricecount; }

    void loadUnits(PriceV5 p) {
        if(units == null) {
            selected = null;
            units = p.units;

            String scode = "";
            if( document != null && document instanceof OrderImpl) {
                OrderItemV5 oi = (OrderItemV5) ((OrderImpl)document).findItem(p.id);
                if( oi != null )
                    scode = oi.unit;
            } else if( units.size() > 0 ) {
                scode = units.get(0).id;
            }

            for(PriceUnit ui : units ) {
                if (ui.id.compareTo(scode) == 0)
                    selected = ui;
            }
        }
    }

    @Override
    protected int getQtyInPack(Price p) {
        loadUnits((PriceV5) p);
        return selected == null ? super.getQtyInPack(p) : selected.inpack;
    }


    @Override
    protected void refreshData() {
        units = null;

        super.refreshData();
        loadUnits((PriceV5) price.getData());

        ArrayAdapter<PriceUnit> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_layout, units);
        Spinner s = (Spinner)findViewById(R.id.spUnits);
        s.setAdapter(adapter);
        if( selected != null ) {
            s.setSelection(units.indexOf(selected));
            onUnitChanged(selected);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final EditText edCount = (EditText)findViewById(R.id.edCount);
        edCount.setSelectAllOnFocus(true);

        Spinner s = (Spinner)findViewById(R.id.spUnits);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                onUnitChanged(units.get(pos));
                edCount.selectAll();
            }
            @Override public void onNothingSelected(AdapterView<?> arg0) {}
        });

        CheckBox cb = (CheckBox)findViewById(R.id.cbPackets);
        cb.setVisibility(View.GONE);
        cb.setChecked(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ((EditText)findViewById(R.id.edCount)).selectAll();
        return super.onTouchEvent(event);
    }

    void onUnitChanged(PriceUnit newUnit) {
        selected = newUnit;
        qtyInPack = newUnit.inpack;
        if( qtyInPack == 0 )
            qtyInPack = Consts.QTY_SCALE;

        TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
        tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));

        updateSumTextView();
    }

    @Override
    protected boolean updateOrder() {
        boolean ret = super.updateOrder();
        if( document instanceof OrderImpl ) {
            OrderItemV5 oi = (OrderItemV5) ((OrderImpl)document).findItem(price.getData().id);
            if( oi != null && selected != null ) {
                oi.unit = selected.id;
                document.write();
            }
        }
        return ret;
    }
}
