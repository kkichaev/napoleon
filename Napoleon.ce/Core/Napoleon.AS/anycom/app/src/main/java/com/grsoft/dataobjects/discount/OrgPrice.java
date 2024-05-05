package com.grsoft.dataobjects.discount;

import android.annotation.SuppressLint;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TableInfo(name="OrgPrice", keyFields = "id,number")
@ServerInfo(name="OrgPrice")
public class OrgPrice extends DataObject {
    public String id = "";
    public int kind = 0;
    public String number = "";
    public String idDsc = "";

    public Date start = new Date();
    public Date finish = new Date();

    public List<OrgPriceItem> items = new ArrayList<>();

    public static Map<String, Integer> load(OrgEx o, OrderEx doc) {
        Map<String, List<OrgPriceWork>> data = new HashMap<>();

//        long docDate = o.getDiscountDate(doc).getTime();
//        long finishDate = docDate - 24 * 3600 * 1000;

        @SuppressLint("DefaultLocale")
        String stmt = String.format(
                "select op.items, op.number, op.idDsc, op.start, op.finish, dp.orgCost kind, items " +
                "from orgprice op, discountpriority dp " +
                "where op.kind = dp.kind and id='%s'"
                ,o.id
        );
//        String stmt = String.format(
//                "select op.items, op.number, op.idDsc, op.start, op.finish, dp.orgCost kind, items " +
//                        "from orgprice op, discountpriority dp " +
//                        "where op.kind = dp.kind and id='%s' and start <= %2$d and finish > %3$d"
//                ,o.id
//                , docDate
//                , finishDate
//        );


        DbReader r = new DbReader();
        OrgPrice op = new OrgPrice();
        boolean bdo = r.selectStmt(op, stmt);
        while(bdo) {
            for(OrgPriceItem opi : op.items) {
                List<OrgPriceWork> opw = data.get(opi.id);
                if(opw == null) {
                    opw = new ArrayList<>();
                    data.put(opi.id, opw);
                }
                opw.add(new OrgPriceWork(op, opi.cost));
            }
            bdo = r.selectNext(op);
        }

        Map<String, Integer> ret = new HashMap<>();
        for(Map.Entry<String, List<OrgPriceWork>> kv : data.entrySet()) {
            Collections.sort(kv.getValue());
            for(OrgPriceWork opw : kv.getValue()) {
                String key = kv.getKey();
                if(!ret.containsKey(key))
                    ret.put(key, opw.cost);
                key += opw.idDsc;
                if(!ret.containsKey(key))
                    ret.put(key, opw.cost);
            }
        }

        return ret;
    }

    static class OrgPriceWork implements Comparable<OrgPriceWork> {
//        public String id = "";
        public int kind = 0;
        public String number = "";
        public String idDsc = "";

        public Date start = new Date();
        public Date finish = new Date();

        public int cost = 0;

        public OrgPriceWork(OrgPrice src, int cost) {
//            id = src.id;
            idDsc = src.idDsc;
            kind = src.kind;
            number = src.number;
            start = src.start;
            finish = src.finish;

            this.cost = cost;
        }

        @Override
        public int compareTo(OrgPriceWork o) {
            if(kind < o.kind) return -1;
            if(kind > o.kind) return 1;

            int cmp = o.start.compareTo(start);
            if(cmp == 0)
                cmp = o.number.compareTo(number);
            return cmp;
        }
    }
}
