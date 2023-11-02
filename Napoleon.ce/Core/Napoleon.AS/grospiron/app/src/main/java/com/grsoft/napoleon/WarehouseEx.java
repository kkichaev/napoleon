package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.MatrixItemsAdapter;

public class WarehouseEx extends Warehouse {
    @Override
    protected BaseAdapter createListAdapter() {
        if(document != null && DocType.getCurDoc() == RemnantsDoc.instance()) {
            OrgImpl oi = new OrgImpl();
            oi.read("id", document.getId());
            OrgEx oe = (OrgEx) oi.getData();
            MatrixImpl mi = new MatrixImpl();
            if(mi.read("name", oe.formatTT)) {
                return new MatrixItemsAdapter(this, mi.getData().items, oe.formatTT);
            }
        }
        return super.createListAdapter();
    }

    protected int getItemLayoutId() {
        if (DocType.getCurDoc() == RemnantsDoc.instance())
            return R.layout.priceitemrowex;
        else
            return  super.getItemLayoutId();
    }

    @Override
    protected void updateChildPriceView(View view, Price p) {
        super.updateChildPriceView(view, p);

        if (DocType.getCurDoc() == RemnantsDoc.instance()) {
            RemnantsImpl rmn = ((RemnantsImpl) document);

            view.findViewById(R.id.llQuant).setVisibility(View.GONE);

            CheckBox cb = view.findViewById(R.id.cbQty);
            cb.setOnCheckedChangeListener(null);
            cb.setChecked(rmn.getItemQty(p) > 0);

            cb.setTag(p.id);
            cb.setOnCheckedChangeListener((v,c)->{
                price.read("id", v.getTag().toString());
                price.close();

                if(c)
                    rmn.updateQty(price, Consts.QTY_SCALE, getCost(price.getData()), false);
                else
                    rmn.deleteItem(price.getData());

                notifyDataSetChanged();
                rmn.write();
                rmn.close();
            });
        }
    }
}
