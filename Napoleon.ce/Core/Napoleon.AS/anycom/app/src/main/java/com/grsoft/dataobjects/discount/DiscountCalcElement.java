package com.grsoft.dataobjects.discount;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Objects;

public class DiscountCalcElement {
    static final int DISCOUNT_SCALE = 1000;

    public String cardNumber = "";
    public String cardName = "";
    public String parent = "";

    public int priority = 0;

    @Scale(value = Consts.SUM_SCALE)
    public int discount = 0;

    public int orgCost = 0;
    public String id = "";
    public String name = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCalcElement that = (DiscountCalcElement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public DiscountCalcElement(DiscountLoad src) {
        discount = src.discount;
        orgCost = src.orgCost;

        cardNumber = src.cardNumber;
        cardName = src.cardName;
        id = src.id;
        name = src.name;

        priority = orgCost == DiscountElement.TYPE_ORG_COST ? src.orgPriority : src.dscPriority;
        parent = src.parent;
    }

    public DiscountCalcElement(DiscountCalcElement src) {
        discount = 0;
        orgCost = src.orgCost;

        cardNumber = src.cardNumber;
        cardName = src.cardName;

        priority = src.priority;
        parent = src.parent;
    }
}
