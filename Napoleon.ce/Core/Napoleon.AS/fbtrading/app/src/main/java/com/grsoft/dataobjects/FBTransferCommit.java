package com.grsoft.dataobjects;

import com.grsoft.database.DbWriter;
import com.grsoft.database.TableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@TableInfo(name="transferAnswers", keyFields = "created")
public class FBTransferCommit extends CreateDocDataObject {

    public String number = "";
    public String whId = "";
    public String agent = "";

    public int direction = FBTransfer.DIRECTION_FROM_ME;

    public List<OrderItem> items = new ArrayList<>();

    static public FBTransferCommit createFrom(FBTransfer src) {
        FBTransferCommit ret = new FBTransferCommit();
        ret.created = src.created;
        ret.date = src.date;
        ret.direction = src.direction;
        ret.userid = src.agent;
        ret.whId = src.whId;
        ret.agent = src.userid;

//        ret.params |= ParamState.ofExported;
        ret.number = src.number;

        for(OrderItem si : src.items) {
            OrderItem di = new OrderItem();
            di.qty = 0;
            di.id = si.id;
            ret.items.add(di);
        }

        DbWriter w = new DbWriter();
        w.setUpsert(false);
        w.insertRecord(ret);
        w.close();

        return ret;
    }

    public void updateQty(List<DeliveryItem> srcI) {
        List<OrderItem> rmv = new ArrayList<>();
        for(OrderItem oi : items) {
            for(DeliveryItem di : srcI) {
                if(oi.id.equals(di.id)) {
                    oi.qty = di.qty;
                    break;
                }
            }
            if(oi.qty == 0)
                rmv.add(oi);
        }

        items.removeAll(rmv);
    }
}
