package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.napoleon.modules.CostHelper;
import com.grsoft.napoleon.modules.CostManager;

public class CreateSalesEx extends CreateSales {
    @Override
    protected int getSalesLayoutId() {
        return R.layout.createsalesex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SalesEx sales = (SalesEx) salesImpl.getData();
        ((CheckBox)findViewById(R.id.cbBlack)).setChecked(sales.black > 0);

        ((CheckBox)findViewById(R.id.cbBlackBonus)).setChecked(sales.blackBonus > 0);

        View v = findViewById(R.id.trCost);
        if( v != null )
            v.setVisibility(View.VISIBLE);
        Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
        CostHelper.loadCostTypes(spPrices, sales.prcType, new CostHelper.CostSelector() {
            @Override
            public void selectedCost(CostManager.CostType costType, int index) {}
        });
    }

    @Override
    protected void init(Sales s, Org org) {
        super.init(s, org);
        s.prcType = ((OrgEx)org).prcType;
        s.sumType = Features.COST_MANAGER.getCostIndex(s.prcType);
        s.number = "";
    }

    @Override
    protected void loadCost() {}

    @Override
    protected void postOkDone(Sales sales) {
        Spinner spPrices = (Spinner) findViewById(R.id.spPrices);
        CostManager.CostType ct = ((CostManager.CostType)spPrices.getSelectedItem());

        if (ct != null) {
            sales.sumType = Features.COST_MANAGER.getCostIndex(ct.id);
            sales.prcType = ct.id;
        }

        boolean isBlack = ((CheckBox)findViewById(R.id.cbBlack)).isChecked();
        ((SalesEx)sales).black = isBlack ? 1 : 0;
        ((SalesEx)sales).blackBonus = (isBlack || ((CheckBox)findViewById(R.id.cbBlackBonus)).isChecked()) ? 1 : 0;
    }
}
