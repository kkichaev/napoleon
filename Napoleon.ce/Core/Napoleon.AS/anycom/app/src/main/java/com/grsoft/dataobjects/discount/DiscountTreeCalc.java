package com.grsoft.dataobjects.discount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiscountTreeCalc extends DiscountTree {

    public List<DiscountTreeCalc> childs = new ArrayList<>();
    public List<DiscountCalcElement> items = new ArrayList<>();

    public void add(DiscountTreeCalc ch) {
        childs.add(ch);
    }

    public void add(DiscountCalcElement ch) {
        items.add(ch);
    }

    public void clear() {
        items.clear();
        childs.clear();
    }

    static int cmpMin(DiscountCalcElement el1, DiscountCalcElement el2) {
        // type_org_cost before
        if(el1.orgCost != el2.orgCost) {
            return el1.orgCost == DiscountElement.TYPE_ORG_COST ? -1 : 1;
        }
        int cmp = Integer.compare(el1.discount, el2.discount);
        return cmp != 0 ? cmp : Integer.compare(el1.priority, el2.priority);
    }

    static int cmpMax(DiscountCalcElement el1, DiscountCalcElement el2) {
        // type_org_cost before
        if(el1.orgCost != el2.orgCost) {
            return el1.orgCost == DiscountElement.TYPE_ORG_COST ? -1 : 1;
        }
        int cmp = Integer.compare(el2.discount, el1.discount);
        return cmp != 0 ? cmp : Integer.compare(el1.priority, el2.priority);
    }

    static int cmpDspl(DiscountCalcElement el1, DiscountCalcElement el2) {
        // type_org_cost before
        if(el1.orgCost != el2.orgCost) {
            return el1.orgCost == DiscountElement.TYPE_ORG_COST ? -1 : 1;
        }
        return Integer.compare(el1.priority, el2.priority);
    }

    DiscountCalcElement sum(List<DiscountCalcElement> src) {
        DiscountCalcElement dce = new DiscountCalcElement(src.get(0));
        for(DiscountCalcElement eli : src) {
            dce.discount += eli.discount;
        }
        return dce;
    }

    DiscountCalcElement mul(List<DiscountCalcElement> src) {
        DiscountCalcElement dce = new DiscountCalcElement(src.get(0));
        double dsc = 1;
        for(DiscountCalcElement eli : src) {
            double cd = (1.0 - eli.discount / 100.0);
            dsc *= cd;
        }
        dce.discount = (int)((1.0 - dsc) * 100 + 0.5);
        return dce;
    }

    DiscountCalcElement calcItems(List<DiscountCalcElement> src) {
        if(src.size() == 0)
            return null;

        if(src.size() > 1) {
            switch (type) {
                case TYPE_MIN:
                    Collections.sort(src, DiscountTreeCalc::cmpMin);
                    break;
                case TYPE_MAX:
                    Collections.sort(src, DiscountTreeCalc::cmpMax);
                    break;
                case TYPE_DSPL:
                    Collections.sort(src, DiscountTreeCalc::cmpDspl);
                    break;
                case TYPE_ADD:
                    return sum(src);
                case TYPE_MUL:
                    return mul(src);
            }
        }

        return src.get(0);
    }

    public DiscountCalcElement calc() {
        List<DiscountCalcElement> data = new ArrayList<>();
        DiscountCalcElement el = calcItems(items);
        if(el != null) {
            data.add(el);
        }
        for(DiscountTreeCalc ci : childs) {
            el = ci.calc();
            if(el != null) {
                data.add(el);
            }
        }

        return calcItems(data);
    }
}
