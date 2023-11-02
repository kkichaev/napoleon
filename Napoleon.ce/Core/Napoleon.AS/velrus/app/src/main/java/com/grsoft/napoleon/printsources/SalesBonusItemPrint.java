package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.SalesItem;

public class SalesBonusItemPrint extends SalesItemPrint{
    public SalesBonusItemPrint(SalesItem item2, int index, int costType) {
        super(item2, index, costType);
        isum = 0;
        isumtax = 0;
        isumwtax= 0;
        costtax = 0;
    }

    @Override
    protected void updateTextFields() {
        super.updateTextFields();
        cost = sum = sumtax = sumwtax = icost = "0.00";
    }
}
