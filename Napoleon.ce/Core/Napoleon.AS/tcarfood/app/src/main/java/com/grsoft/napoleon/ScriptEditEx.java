package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.IncassDebDistr;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.IncassDebDistrImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;


public class ScriptEditEx extends ScriptEdit {
    private int curpos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void openDoc(int position) {
        ScriptDefItem item =  def.getData().items.get(position);

        if (doc.IsEnabled(position, def.getData()) && item.curType.equals(OrderDoc.instance().getObjectName()) ) {
            int sum = getDebtSum();

            if (sum > 0) {
                clickedPos = position;
                createIncass(sum);
                return;
            }
        }

        OrgImpl oi = new OrgImpl();
        oi.getData().id = doc.getId();
        oi.read();
        oi.close();
        OrgEx org = (OrgEx)oi.getData();
        DeliveryInfo di = DeliveryInfo.collectDelivery(doc.getId());

        if(org.limitsum > 0 && item.curType.equals(OrderDoc.instance().getObjectName()) &&	(di.hasExceed || di.sum >= org.limitsum)) {
            curpos = position;
            showDialog(R.id.has_exceed_delivery_dlg);
            return;
        }

        super.openDoc(position);
    }

    private int getDebtSum() {
        OrgImpl oi = new OrgImpl();
        oi.getData().id = doc.getId();
        oi.read();
        oi.close();

        if (!oi.getData().isStopList())
            return 0;

        String where = String.format("created > %d and ((params & 1) == 1) and id = '%s'",
                Util.resetTime(new Date()).getTime(), doc.getId());

        java.util.List sums = new ArrayList<Integer>();

        DataTraveler.travel(IncassDebDistr.class, new DataTraveler.Travel<IncassDebDistr>() {
            @Override
            public boolean travel(DataTraveler<IncassDebDistr> item) {
                sums.add(item.data.sum);

                return true;
            }
        }, where);

        int sum  = 0;
        for (Object i : sums)
            sum += (Integer)i;

        return ((OrgEx)oi.getData()).postdue - sum;
    }

    private void createIncass(int sum) {
        IncassDebDistrImpl incass = (IncassDebDistrImpl)IncassDoc.instance().create();
        incass.init(this, doc.getId(), new GpsCoord(doc.getData().latitude, doc.getData().longitude, doc.getData().stltime));

        Intent i = new Intent(this, IncassPrompt.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, incass.getRowid());
        i.putExtra(IncassPrompt.PDUE, sum);
        startActivityForResult(i, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK)
            super.openDoc(clickedPos);
        else
            ((ScriptImplEx) doc).setBlocked(clickedPos);
    }

    private int clickedPos = 0;

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.has_exceed_delivery_dlg) {
            return new ExceedDeliveryDialogFactory().createDialog(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    docOpenning(curpos);
                }
            });
        }else
            return super.onCreateDialog(id);
    }

}
