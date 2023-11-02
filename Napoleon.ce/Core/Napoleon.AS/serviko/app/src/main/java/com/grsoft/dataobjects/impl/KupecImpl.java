package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Kupec;
import com.grsoft.dataobjects.KupecItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

import java.util.List;

public class KupecImpl extends CreatableDocument<Kupec> {
    @Override
    public void open(Context context) {
    }

    @Override
    public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
        return super.init(context, orgId, gpsCoord);
    }

    @Override
    public void postInit() {
        super.postInit();
        OrgImpl org = new OrgImpl();
        org.read("id", data.id);
        data.regionID = ((OrgEx)org.getData()).regionID;
    }

    public static KupecImpl getLastDoc(Context context, String orgId, GpsCoord gpsCoord){
        String tn = DataObjectInfo.getInstance().getTableName(Kupec.class);
        String condition = "id='" + orgId + "' AND params = 0";
        DbWriter.checkDBTable(getDataType(Remnants.class));
        List<Long> ids = DbReader.readIds(tn, condition, null);

        KupecImpl res = new KupecImpl();

        if (ids.size() > 0){
            res.read(ids.get(0));
            return res;
        }

        res.init(context, orgId, gpsCoord);
        return res;
    }

    public boolean addID(String id){
        for (KupecItem i : data.items)
            if (i.id.equals(id)){
                data.items.remove(i);
                return false;
            }

        KupecItem i = new KupecItem();
        i.id = id;

        data.items.add(i);
        return true;
    }

    public boolean hasID(String id){
        for (KupecItem i : data.items)
            if (i.id.equals(id)){
                return true;
            }

        return false;
    }

    @Override
    public boolean isEmpty() {
        return data.items.size() == 0;
    }
}
