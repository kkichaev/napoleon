package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

@TableInfo(name = "Price", keyFields = "id", indexes = "barcode")
public class PriceEx extends Price {
    public String barcode = "";
    public List<GrassDiscount> discounts = new ArrayList<>();

    public void addCost(int price) {
        if(cost.size() == 0) {
            cost.add(new CostItem());
        }
        cost.get(0).cost = price;
    }

    public PriceUnit getUnit(String unitCode) {
        for(PriceUnit u : units) {
            if(u.id.equals(unitCode)) {
                return u;
            }
        }
        PriceUnit u = new PriceUnit();
        u.name = unitCode;
        u.inpack = Consts.QTY_SCALE;
        return u;
    }
}
