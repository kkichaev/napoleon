package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TableInfo(name="SenegInputDoc", keyFields = "link")
@ServerInfo(name="SenegInputDoc")
public class SenegInputDoc extends DocDataObject {
    public static final int TYPE_DOGOVOR = 1;
    public static final int TYPE_ORDER = 2;
    public static final int TYPE_RECONCILATION = 3;
    public static final int TYPE_INVENT = 4;
    public static final int TYPE_RETURN = 5;
    public static final int TYPE_DELIVERY = 6;

    public int docType = 0;
    public String number = "";
    public String link = "";
    public String remark = "";
    public String dogovor = "";

    public List<SenegDocItem> items = new ArrayList<>();

    public static String getType(int docType) {
        switch (docType) {
            case TYPE_DOGOVOR:
                return "Договор";
            case TYPE_ORDER:
                return "Заказ";
            case TYPE_RECONCILATION:
                return "Акт сверки";
            case TYPE_INVENT:
                return "Инвентаризация";
            case TYPE_RETURN:
                return "Возврат";
            case TYPE_DELIVERY:
                return "Накладная";
        }
        return "";
    }

    public String typeToStr() {
        String ret = getType(docType);
        return ret + " " + number;
    }

    public int weight() {
        int ret = 0;
        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();

        for(SenegDocItem sdi : items) {
            p.id = sdi.id;
            pi.read();
            ret += (int)((long)sdi.qty * p.weight / Consts.QTY_SCALE);
        }

        pi.close();
        return ret;
    }

    public long sum() {
        long ret = 0;
        for(SenegDocItem sdi : items) {
            ret += sdi.sum;
        }
        return ret;
    }

    public boolean canEdit() {
        return (Util.getDayStart(date).compareTo(Util.getDate()) == 0);
    }
}
