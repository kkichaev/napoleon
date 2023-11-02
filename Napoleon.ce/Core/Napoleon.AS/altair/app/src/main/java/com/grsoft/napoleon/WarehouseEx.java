package com.grsoft.napoleon;

import android.view.View;
import android.widget.ImageView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.CostItem;

public class WarehouseEx extends Warehouse {

    @Override
    protected int getItemLayoutId() {
        return R.layout.priceitemrowex;
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        View v = super.getPriceView(node, convertView);
        int res = R.drawable.empty;
        if(document != null) {
            int ac = ((CostStrategyEx) CostStrategy.defaultInstance).getActionCost(price.getData(), document.getSumType());
            if(ac > 0) {
                res = R.drawable.action;
            }
        }
        ((ImageView)v.findViewById(R.id.iAction)).setImageResource(res);
        return v;
    }
}
