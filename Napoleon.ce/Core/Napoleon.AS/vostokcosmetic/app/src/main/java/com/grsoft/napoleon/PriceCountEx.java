package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {

    @Override protected int getContentViewId() { return R.layout.pricecountex; }

    @Override
    protected void refreshData() {
        super.refreshData();
        TextView tv;
        PriceEx pe = (PriceEx) price.getData();

        tv = findViewById(R.id.tvNesting);
        tv.setText(pe.nesting);

        tv = findViewById(R.id.tvBarcode);
        tv.setText("ÿ : " + pe.barcode);

        if(document instanceof OrderImplEx) {
            findViewById(R.id.trDsc).setVisibility(View.VISIBLE);
            findViewById(R.id.trDscCost).setVisibility(View.VISIBLE);

            CostStrategyEx ce = (CostStrategyEx)CostStrategy.defaultInstance;
            int dsc = ce.discount(price.getData(), (OrderEx) document.getData());
            int prcCost = ce.getPriceCost(price.getData(), document);
            tv = findViewById(R.id.tvDiscPrice);
            tv.setText(Util.IntToScaleStr(priceVal, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            tv = findViewById(R.id.tvDisc);
            tv.setText(Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            tv = findViewById(R.id.tvPrice);
            tv.setText(Util.IntToScaleStr(prcCost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        }
    }
}
