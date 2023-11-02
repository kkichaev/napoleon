package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends Warehouse{
    static int whIndex = 0;

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        if (DocType.getCurDoc() == OrderDoc.instance()) {
            View res = super.getPriceView(node, convertView);
            price.read(node.getRowid());
            price.close();
            Integer qty = ((OrderImplEx)document).getItem2Value(price.getData());
            TextView tv = (TextView)res.findViewById(R.id.tvRezerv);
            tv.setText(qty == null ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE));
            return res;
        }else
            return super.getPriceView(node, convertView);
    }

    @Override protected int getItemLayoutId() {
        if (DocType.getCurDoc() == OrderDoc.instance())
            return R.layout.priceitemrowex;
        else
            return super.getItemLayoutId();
    }

    @Override
    protected Filter createZeroPositionFilter() {
        if( document instanceof OrderImplEx ) {
            if( whIndex != ((Order)document.getData()).supplyer) {
                whIndex = ((Order)document.getData()).supplyer;
                FoldersAdapter.resetCache();
            }
        } else if( whIndex != 0 ) {
            whIndex = 0;
            FoldersAdapter.resetCache();
        }

        return new ZeroFilter();
    }

    class ZeroFilter extends ZeroPositionFilter {

        @Override public String getWhereStr() { return ""; }

        @Override
        public boolean inset(long priceRowID, String id) {
            if( !(document instanceof Itemsable) )
                return super.inset(priceRowID, id);

            boolean result = false;
            if(price.read(priceRowID))
                result = (((Itemsable)document).getItemValue(price.getData()) > 0);
            return result;
        }
    }

}
