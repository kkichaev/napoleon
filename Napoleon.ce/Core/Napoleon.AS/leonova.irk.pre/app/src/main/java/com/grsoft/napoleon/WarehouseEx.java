package com.grsoft.napoleon;

import android.widget.Toast;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;

public class WarehouseEx extends Warehouse {
    @Override
    protected int getDefaultColor(Price p) {
        if (document.getRowid() != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance())
            if (((PriceEx) p).merc == 1 && ((PriceEx) p).chznak == 1)
                return getResources().getColor(R.color.mercchznak);
            else if (((PriceEx) p).merc == 1)
                return getResources().getColor(R.color.merc);
            else if (((PriceEx) p).chznak == 1)
                return getResources().getColor(R.color.chznak);
            else
                return super.getDefaultColor(p);
        else
            return super.getDefaultColor(p);
    }

    @Override
    public void editItem(long rowid) {
        if (document.getRowid() != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance()) {
            price.read(rowid, false);
            OrgImpl org = new OrgImpl();
            org.read("id", orgid);

            if (((PriceEx) price.getData()).merc == 1 && ((PriceEx) price.getData()).chznak == 1
                    && (((OrgEx) org.getData()).merc == 0 || ((OrgEx) org.getData()).chznak == 0)) {
                Toast.makeText(this, R.string.mercurychznak_error, Toast.LENGTH_SHORT).show();
                return;
            } else if (((PriceEx) price.getData()).merc == 1 && ((OrgEx) org.getData()).merc == 0) {
                Toast.makeText(this, R.string.mercury_error, Toast.LENGTH_SHORT).show();
                return;
            } else if (((PriceEx) price.getData()).chznak == 1 && ((OrgEx) org.getData()).chznak == 0) {
                Toast.makeText(this, R.string.chznak_error, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        super.editItem(rowid);
    }
}
