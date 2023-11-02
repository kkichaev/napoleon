package com.grsoft.napoleon.dostavka;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.OrgTare;
import com.grsoft.dataobjects.RoutePointEx;
import com.grsoft.dataobjects.impl.DTareImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RouteImpl;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.util.ExtrasConst;

import java.util.HashMap;
import java.util.Map;

public class DTareEdit extends DShipmentEdit {

    Map<String, OrgTare> tare = new HashMap<>();

    public static void open(Context context, DTareImpl doc) {
        Intent intent = new Intent(context, DTareEdit.class);
        intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        intent.putExtra(DWaybillEdit.DOCTYPE, doc.getClass());
        context.startActivity(intent);
    }

    @Override protected int getLayoutID() { return R.layout.tare_edit;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoutePointImpl oi = new RoutePointImpl();
        RoutePointEx rp = (RoutePointEx) oi.getData();
        rp.id = doc.getId();
        oi.read();
        oi.close();

        for(OrgTare ot : rp.tare) {
            tare.put(ot.id, ot);
        }
    }

    @NonNull
    @Override
    public Adapter createAdapter() {
        return new TareAdapter();
    }

    @Override
    public int getItemLayout() {
        return R.layout.tare_edit_row;
    }

    class TareAdapter extends Adapter {

        @Override
        protected void setView(DWaybillDocumentItem i, View convertView, int color, int position) {
            TextView tv;
            OrgTare ot = tare.get(i.id);

            String text = ot == null ? String.format("<%s>", i.id) : ot.name;
            tv = (TextView) convertView.findViewById(R.id.tvName);
            tv.setText(text);
            tv.setTextColor(color);

            text = ot == null ? "" : ot.number;
            tv = (TextView) convertView.findViewById(R.id.tvTareNumber);
            tv.setText(text);
            tv.setTextColor(color);

            CheckBox loaded = convertView.findViewById(R.id.loaded);
            boolean contains = ((DTareImpl)doc).contains(i.id);
            loaded.setChecked(contains);

            if(doc.isEditable()) {
                loaded.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    ((DTareImpl) doc).update(i.id, isChecked);
                });
            }
        }
    }
}
