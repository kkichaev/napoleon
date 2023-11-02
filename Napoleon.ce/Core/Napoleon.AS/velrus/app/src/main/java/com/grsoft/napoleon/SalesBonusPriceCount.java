package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.ExtrasConst;

public class SalesBonusPriceCount extends PriceCount{
    public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
        Intent i = new Intent(context, SalesBonusPriceCount.class);

        i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

        context.startActivity(i);
    }

    @Override
    protected void refreshData() {
        super.refreshData();
//        findViewById(R.id.layoutAction).setVisibility(View.GONE);
//        findViewById(R.id.cbPackets).setEnabled(true);
    }
}
