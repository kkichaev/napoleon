package com.grsoft.dataobjects;

import com.grsoft.util.Consts;

public class RemnantsEx extends Remnants {
    public int display = 0;
//    public int complete = 0;

    public void updateDisplay() {
        display = 0;
        for(RemnantItem ri : items) {
            display += ri.qty;
        }
        display /= Consts.QTY_SCALE;
    }
};
