package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgNotes;
import com.grsoft.dataobjects.OrgNotesEx;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgNotesImpl;
import com.grsoft.network.ObjectExportListener;

import java.util.ArrayList;
import java.util.List;

public class OrgNotesHitching extends Hitching implements ObjectExportListener {
    List<Long> list;

    public OrgNotesHitching() {
        super(OrgNotes.class, "OrgNotes");
        DbWriter.checkDBTable(DbObject.getDataType(OrgNotes.class));
        list = new ArrayList<Long>();
        String where = "(([params] & " + ParamState.ofExported + " ) == 0) or params is null";
        list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(OrgNotes.class), where, "");
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public void onEnd() {
        for (int i = 0; i < list.size(); i++) {
            OrgNotesImpl impl = new OrgNotesImpl();
            impl.read(list.get(i));
            ((OrgNotesEx) impl.getData()).params |= ParamState.ofExported;
            impl.write();
            impl.close();
        }
    }

    @Override
    public DataObject get(int i) {
        OrgNotesImpl impl = new OrgNotesImpl();
        impl.read(list.get(i));
        impl.close();
        return impl.getData();
    }
}
