package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="GoodsDiscount", keyFields = "id,org")
@ServerInfo(name="GoodsDiscount")
public class GoodsDiscount extends DataObject {
    public String id = "";
    public String org = "";

    public Date start = new Date();
    public Date end = new Date();

    public List<GoodsDiscountItem> items = new ArrayList<>();

    public int getCost(String id) {
        for(GoodsDiscountItem i : items) {
            if(i.id.equals(id))
                return i.cost;
        }
        return 0;
    }
}
