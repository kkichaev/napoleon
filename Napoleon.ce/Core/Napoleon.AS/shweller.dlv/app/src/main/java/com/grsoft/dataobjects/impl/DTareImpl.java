package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.dataobjects.OrgTare;
import com.grsoft.dataobjects.TareReq;
import com.grsoft.dataobjects.TareReturn;
import com.grsoft.dataobjects.TareReturnItem;
import com.grsoft.napoleon.dostavka.DTareEdit;
import com.grsoft.util.GpsCoord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTareImpl extends DWaybillDocumentImpl<TareReturn> {

    @Override
    public void open(Context context) {
        DTareEdit.open(context, this);
    }

    public boolean contains(String id) {
        boolean cnt = false;
        for(DWaybillDocumentItem i : data.items) {
            if(i.id.equals(id)) {
                return ((TareReturnItem)i).loaded > 0;
            }
        }
        return false;
    }

    @Override
    public boolean init(Context context, DispatchImpl doc, DispatchItem item, GpsCoord loc) {
        List<TareReq> src = DbReader.fetch(TareReq.class, String.format("id='%s' and number='%s'"
                ,doc.getId()
                ,item.number));

        for(TareReq r : src) {
            for(DeliveryItem di : r.items){
                TareReturnItem i = new TareReturnItem();
                initWaybillItem(di, i);
                i.loaded = 1;

                data.items.add(i);
            }
            break;
        }
        return superInit(context, doc, item, loc);
    }

    public void update(String id, boolean isChecked) {
        for(DWaybillDocumentItem i : data.items) {
            if(i.id.equals(id)) {
                ((TareReturnItem)i).loaded = isChecked ? 1 : 0;
                break;
            }
        }
    }
}
