package com.serviko.sales.main_views.price_filter;

import com.serviko.dataobjects.Price;
import com.serviko.dataobjects.priceTree.TreeElement;
import com.serviko.sales.main_views.Filter;

import java.util.Comparator;

public class PriceOrdering extends Filter implements Comparator<TreeElement> {
    public static int SORT_ASC = 0;
    public static int SORT_DESC = 1;
    public static int SORT_DISCOUNT = 2;

    public static String SORT_ASC_STR = "asc";
    public static String SORT_DISCOUNT_STR = "discount";
    public static String SORT_DESC_STR = "desc";

    public int sortMode = SORT_ASC;

    @Override
    public int compare(TreeElement p1, TreeElement p2) {
        if(sortMode == SORT_ASC) return (int)(p1.item.cost - p2.item.cost);
        if(sortMode == SORT_DESC) return (int)(p2.item.cost - p1.item.cost);
        return (int)(p2.item.discount - p1.item.discount);
    }

    @Override
    public boolean getValue(String fieldName) {
        if(fieldName.equals(SORT_ASC_STR))
            return sortMode == SORT_ASC;
        if(fieldName.equals(SORT_DESC_STR))
            return sortMode == SORT_DESC;
        if(fieldName.equals(SORT_DISCOUNT_STR))
            return sortMode == SORT_DISCOUNT;
        return false;
    }

    @Override
    public void setValue(String fieldName, boolean value) {
        if(!value)
            return;

        if(fieldName.equals(SORT_ASC_STR))
            sortMode = SORT_ASC;
        else if(fieldName.equals(SORT_DESC_STR))
            sortMode = SORT_DESC;
        else if(fieldName.equals(SORT_DISCOUNT_STR))
            sortMode = SORT_DISCOUNT;
    }
}
