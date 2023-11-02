package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.napoleon.documents.PurchaseDoc;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class ScriptImplEx extends ScriptImpl {

    public void initData(Context c, String orgId, GpsCoord gpsCoord, ScriptDef scriptDef) {
        OrgImpl oi = new OrgImpl();
        oi.read("id", orgId);
        OrgEx oe = (OrgEx) oi.getData();

        if(!oe.isPerson())
            ((ScriptEx)data).address = oe.address;
        else {
            ((ScriptEx)data).clientType = oe.orgType;
        }

        data.scriptId = scriptDef.id;
        for(ScriptDefItem si : scriptDef.items) {
            ScriptItem di = new ScriptItem(si);
            data.items.add(di);
        }

        ((ScriptEx)data).finish = Util.getDateTime();

        initData(c, orgId, gpsCoord);
    }

    @Override
    public long write() {
        updatePurchase();
        return super.write();
    }

    private void updatePurchase() {
        for (ScriptItem i : data.items){
            if (i.type.equals(PurchaseDoc.instance().getObjectName()) && i.state == ScriptItem.DOC_INITED){
                PurchaseImpl p = new PurchaseImpl();
                if (p.read(i.date.getTime())){
                    p.data.payType = ((ScriptEx)data).payType;
                    p.write();
                }

                p.close();
            }
        }
    }

    public void closeScript() {
        ((ScriptEx)data).finish = Util.getDateTime();
        write();
        close();
    }

    public boolean isSigned(){
        boolean res = false;
        VisitImplEx refVisit = new VisitImplEx();
        refVisit.getData().created = ((ScriptEx)data).visitDoc;

        if (refVisit.read())
            res = refVisit.hasSignature();

        refVisit.close();

        return res;
    }

    @Override
    public boolean delete() {
        VisitImpl visit = new VisitImpl();

        if (visit.read(((ScriptEx)data).visitDoc.getTime()))
            visit.delete();

        visit.close();

        return super.delete();
    }
}
