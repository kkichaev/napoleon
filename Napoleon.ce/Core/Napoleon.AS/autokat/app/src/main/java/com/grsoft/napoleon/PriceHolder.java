package com.grsoft.napoleon;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;

import java.util.Map;

public class PriceHolder {
    static Map<Object, PriceEx> price = null;

    public static void clear() {
        price = null;
    }
    public static PriceEx get(String id) {
        if(price == null) {
            price = DbReader.fetchDic(PriceEx.class, "id");
        }

        PriceEx ret = price.get(id);
        if(ret == null) {
            ret = new PriceEx();
            ret.id = id;
            ret.name = id;
        }
        return ret;
    }
}
