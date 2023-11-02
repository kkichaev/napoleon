package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.List;

@TableInfo(name="gtin", keyFields = "barcode,id")
@ServerInfo(name="GTIN")
public class Gtin extends DataObject {
    public String id = "";
    public String barcode = "";
    @Scale(value= Consts.QTY_SCALE)
    public int qty = 0;

    public static Gtin findGtin(String id, List<Gtin> gtins) {
        for(Gtin g : gtins) {
            if(g.id.equals(id))
                return g;
        }
        return new Gtin();
    }

}
