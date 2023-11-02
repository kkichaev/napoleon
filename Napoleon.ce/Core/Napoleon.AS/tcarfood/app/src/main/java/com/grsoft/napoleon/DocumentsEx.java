package com.grsoft.napoleon;

import android.app.Dialog;
import android.content.DialogInterface;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
    private DeliveryInfo deliveryInfo;


    protected String orgInfo(com.grsoft.dataobjects.Org o) {
        deliveryInfo = DeliveryInfo.collectDelivery(o.id);
        OrgEx oe = (OrgEx) o;

        if (oe.limitsum == 0)
            return super.orgInfo(o);

        StringBuilder sb = new StringBuilder(super.orgInfo(o));
        sb.append("<br>");
        sb.append(getString(R.string.debt_info,
                Util.IntToScaleStr(oe.limitsum, Consts.SUM_SCALE),
                Util.IntToScaleStr(deliveryInfo.sum, Consts.SUM_SCALE),
                Util.IntToScaleStr(oe.limitsum - deliveryInfo.sum, Consts.SUM_SCALE)));
        return sb.toString();
    }

    @Override
    protected void doCreate() {
        if (DocType.getCurDoc() == OrderDoc.instance() && (deliveryInfo.hasExceed || deliveryInfo.sum >= ((OrgEx) org.getData()).limitsum))
            showDialog(R.id.has_exceed_delivery_dlg);
        else
            super.doCreate();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.has_exceed_delivery_dlg)
            return new ExceedDeliveryDialogFactory().createDialog(this, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    docCreating();
                }
            });
        else
            return super.onCreateDialog(id);
    }
}
