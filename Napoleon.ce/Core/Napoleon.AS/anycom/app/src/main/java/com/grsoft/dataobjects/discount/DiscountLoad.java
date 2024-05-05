package com.grsoft.dataobjects.discount;

import android.annotation.SuppressLint;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.util.FolderTree;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiscountLoad extends Discount {
    public String cardNumber = "";
    public String cardName = "";

    public int orgPriority = 0;
    public int dscPriority = 0;

    public static List<DiscountLoad> load(OrgEx o, OrderEx doc) {
        long foreverMarker = 24 * 2 * 3600 * 1000;
        long docDate = o.getDiscountDate(doc).getTime();
        long finishDate = docDate - 24 * 3600 * 1000;


        @SuppressLint("DefaultLocale")
        String stmt = String.format(
            "select d.*, dp.discount dscPriority, dp.orgCost orgPriority, cc.name cardName, cc.number cardNumber " +
            "from (select * from Discount where id in " +
            "(select distinct od.id from " +
            "(select od.id from OrgDiscount od where od.idOrg = '%1$s' " +
            "union all select d.id from Discount d left join OrgDiscount od on d.id = od.id where od.idOrg is null) od, " +
            "(select sd.id from StoreDiscount sd where sd.idStore = '%2$s' " +
            "union all select d.id from Discount d left join StoreDiscount od on d.id = od.id where od.idStore is null) sd " +
            "where od.id = sd.id) " +
            "and start <= %4$d and (finish < %3$d or finish > %5$d)) d " +
            "left join (select * from ClientCard where start <= %4$d and (finish < %3$d or finish > %5$d)) cc on d.id = cc.idDsc, " +
            "DiscountPriority dp where d.kind = dp.kind",
                o.id
                ,doc.whCode
                ,foreverMarker
                ,docDate
                ,finishDate
                );

        List<DiscountLoad> res = new ArrayList<>();

        DbReader r = new DbReader();
        DiscountLoad data = new DiscountLoad();
        boolean bdo = r.selectStmt(data, stmt);
        while(bdo) {
            res.add(data);

            data = new DiscountLoad();
            bdo = r.selectNext(data);
        }
        r.close();

        return res;
    }
}
