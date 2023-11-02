package com.grsoft.dataobjects;

import java.util.Date;

public class BalanceDoc extends DocDataObject implements Comparable<BalanceDoc> {

    public BalanceItem src;

    public BalanceDoc() { src = new BalanceItem(); }

    public BalanceDoc(Balance doc, BalanceItem src) {
        this.src = src;

        id = doc.id;
        date = src.date;
    }

    public boolean isOverdue() {
        return src.isDelivery() && src.sum >0 && src.payDate.compareTo(new Date()) < 0;
    }

    public long sum() { return src.sum; }

    @Override
    public int compareTo(BalanceDoc o) {
        int cmp = id.compareTo(o.id);
        if(cmp == 0)
            cmp = o.date.compareTo(date);
        return cmp;
    }
}
