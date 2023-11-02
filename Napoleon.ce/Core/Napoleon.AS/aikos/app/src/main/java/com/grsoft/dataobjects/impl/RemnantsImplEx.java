package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.RemnantItemEx;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.dataobjects.Supplier;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.InputNumber;

import java.util.List;

public class RemnantsImplEx extends RemnantsImpl {

    String aikosID;

    public int prevDisplay() {
        String where = String.format("id='%s' and created < %d", data.id, data.created.getTime());
        List<RemnantsEx> els =  DbReader.fetch(RemnantsEx.class, where, "created desc");
        return els.size() > 0 ? els.get(0).display : 0;
    }

    public int countAikos() {
        int c = 0;
        for (RemnantItem ri : data.items) {
            if (((RemnantItemEx)ri).face > 0)
                c += ri.qty;
        }

        return c / Consts.QTY_SCALE;
    }

    public void edit(String id, final Context context) {
        InputNumberDlg.open(context, new InputNumber() {
            @Override
            public void applayInput(int value, Object... params) {
                if (isExported())
                    return;

                PriceImpl pi = new PriceImpl();
                PriceEx pe = (PriceEx) pi.getData();
                pe.id = id;
                boolean refresh = false;
                if( value == 0 && editValue.length() == 0) {
                    refresh = deleteItem(pe);
                } else {
                    refresh = updateQty(pi, value, 0, false);
                }
                if (refresh && context instanceof DataSetNotify)
                    ((DataSetNotify)context).notifyDataSetChanged();

                RemnantsDoc.instance().refreshDocSum(data.id);
            }

            @Override
            public long getValue() {
                RemnantItemEx ri = (RemnantItemEx) findItem(id);
                return  ri == null ? 0 : ri.qty;
            }
        });
    }

    @Override
    protected void beforeWrite(RemnantItem item) {
        if(aikosID == null) {
            aikosID = Supplier.aikosId();
        }
        if(item.id.endsWith(aikosID)) {
            ((RemnantItemEx)item).face = 1;
        }
        ((RemnantsEx)data).updateDisplay();
    }
}
