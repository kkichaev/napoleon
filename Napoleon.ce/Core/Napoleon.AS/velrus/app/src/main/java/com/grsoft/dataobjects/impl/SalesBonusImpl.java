package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.SalesBonus;
import com.grsoft.napoleon.EditBonusProps;
import com.grsoft.napoleon.SalesBonusDetail;
import com.grsoft.napoleon.SalesBonusPriceCount;

public class SalesBonusImpl extends SalesBaseImpl<SalesBonus> {
    @Override
    public void editProperties(Context ctx, boolean isOldOrder) {
        EditBonusProps.open(ctx, this, isOldOrder   );
    }

    @Override
    public void open(Context context) {
        SalesBonusDetail.open(context, this);
    }

    @Override
    protected boolean checkPriceQty() {
        return false;
    }

    @Override
    public void editItem(long itemRowid, Context context) {
        SalesBonusPriceCount.open(context, itemRowid, this);
    }
}
