package com.grsoft.dataobjects;

import com.grsoft.database.DbReader;
import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

import java.util.Date;
import java.util.List;

@TableInfo(name="PicStore", keyFields = "created")
public class PicStoreSrc extends CreateDocDataObject {
    public String name = "";

    public static String get(String userid, Date created) {
        String name = null;

        String where = String.format("userid = '%s' and created = %d", userid, created.getTime());

        List<PicStoreSrc> src = DbReader.fetch(PicStoreSrc.class, where);
        for(PicStoreSrc s : src) {
            name = s.name;
            break;
        }
        return name;
    }
}
