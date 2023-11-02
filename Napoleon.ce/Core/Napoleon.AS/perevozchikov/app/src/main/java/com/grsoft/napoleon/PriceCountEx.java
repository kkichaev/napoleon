package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.Toast;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
    int maxDiscount = 0;
    int minCost = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConfigImpl ci = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        if(ci.getValue(sb,"Скидка")) {
            maxDiscount = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        if(maxDiscount > 0) {
            int pc = CostStrategy.defaultInstance.getPriceCost(price.getData(),
                    document == null ? 0 : document.getSumType(),
                    document);

            minCost = CostStrategy.costWithDiscount(pc, maxDiscount, Consts.SUM_SCALE);
        }
    }

    @Override
    protected void onChangeCost(int newCost) {
        if(newCost < minCost) {
            Toast.makeText(this, "Цена ниже минимальной", Toast.LENGTH_LONG).show();
            return;
        }
        super.onChangeCost(newCost);
    }

    @Override
    protected boolean canChangeCost() {
        return maxDiscount > 0;
    }
}
