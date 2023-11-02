package com.grsoft.dataobjects;

import com.grsoft.database.DbWriter;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.Date;

@TableInfo(name="transfers", keyFields = "created")
public class FBTransfer extends Order {
    public static final int STATE_REQUESTED = 0;

    public static final int DIRECTION_TO_ME = 0;
    public static final int DIRECTION_FROM_ME = 1;

    public String whId = "";
    public String agent = "";

    public int direction = DIRECTION_FROM_ME;

    public int needCommit1c = 0;

    public static FBTransfer createFrom(FBTransferCommit src) {
        // Этот вариант предполагается только для документов созданных в 1с (там agent не заполняется)
        FBTransfer dest = new FBTransfer();
        dest.created = Util.getDateTime();
        dest.date = Util.getDate();
        dest.whId = src.whId;
        dest.agent = "";
        dest.direction = src.direction;
        dest.needCommit1c = 1;
        dest.number = src.number;
        dest.direction = src.direction;
        dest.remark = src.remark;
        dest.params |= ParamState.ofExported;

        for(OrderItem si : src.items) {
            OrderItem di = new OrderItem();
            di.id = si.id;
            di.qty = si.qty;

            dest.items.add(di);
        }

        DbWriter w = new DbWriter();
        w.setUpsert(false);
        while(w.insertRecord(dest) == ExtrasConst.INVALID_ID) {
                dest.created = new Date(dest.created.getTime() + 1000);
        }
        w.close();

        return dest;
    }

    public String sendObjectName() {
        if(agent.length() != 0)
            return "ReqTransfer";
        return "ReqTransfer1c";
    }

    public boolean isMyRequest() {
        AgentsEx me = AgentsEx.me();
        return !agent.equals(me.id);
    }

    public boolean needAccept(FBTransferCommit ref) {
        if(ref == null) return !isMyRequest();
        return false;
    }

    public boolean needAccept1c(FBTransferCommit ref) {
        return (ref != null && ref.number.length() > 0 && needCommit1c > 0 );
    }

    public boolean commitedByAgent(FBTransferCommit ref) {
        return ref != null && ref.number.length() == 0;
    }

    public boolean commitedBy1c(FBTransferCommit ref) {
        return ref != null && ref.number.length() != 0;
    }


    public String stateText(FBTransferCommit ref, FBTransferReject rej) {
        if(rej != null) {
            return "Отказ " + rej.remark;
        }

        String ret = "";
        if(ref == null) {
            ret =  needAccept(ref) ? "Запрос перемещения"  :
                    DocumentUtils.isExported(params) ? "Ожидание подтверждения" :
                    "Новый документ";
        } else {
            if (ref.number.length() == 0) ret = "Подтвержден";
            else {
                ret = "1с № " + ref.number + " от " + Util.simpleDateFormat.format(ref.date);
                if (needAccept1c(ref)) {
                    ret += "<br/>требуется подтверждение";
                }
            }
        }
        if(remark.length() > 0) {
            ret += "<br/>" + remark;
        }
        return ret;
    }
}
