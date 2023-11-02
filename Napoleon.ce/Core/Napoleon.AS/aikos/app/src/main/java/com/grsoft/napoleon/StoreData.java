package com.grsoft.napoleon;

import com.grsoft.util.view.dialog_helper.KeyValue;

public class StoreData extends KeyValue {
    public int qty;
    public int idx;
    public StoreData(KeyValue src, int qty, int idx) {
        super(src.key.toString(), src.value.toString());
        this.qty = qty;
        this.idx = idx;
    }
}
