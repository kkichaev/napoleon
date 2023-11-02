package com.grsoft.database;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PicStore;
import com.grsoft.napoleon.main.SignHelper;

import java.util.List;

public class PicStoreHitchingEx extends PicStoreHitching{
    @Override
    public void fetch() {
        super.fetch();

        DbWriter.checkDBTable(PicStore.class);
        String table = DataObjectInfo.getInstance().getTableName(PicStore.class);
        StringBuilder sb = new StringBuilder();
        sb.append("created = ");
        sb.append(SignHelper.minePicStore.getTime());
        List<Long> list = DbReader.readIds(table, sb.toString(), null);

        if (list.size() > 0) {
            long id = list.get(0);

            if (!ids.contains(id))
                ids.add(id);
        }
    }
}
