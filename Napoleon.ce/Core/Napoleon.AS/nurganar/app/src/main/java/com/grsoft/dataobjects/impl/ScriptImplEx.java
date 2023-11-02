package com.grsoft.dataobjects.impl;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Equip;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.InvEquDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

import java.util.ArrayList;
import java.util.List;

public class ScriptImplEx extends ScriptImpl {
    @Override
    public boolean isSkipped(int position) {
        boolean result = super.isSkipped(position);

        if( !result && position >= 0 && position < data.items.size()  ){
            ScriptItem item = data.items.get(position);

            if (item.type.equals(InvEquDoc.OBJ_NAME)){
                DbWriter.checkDBTable(Equip.class);

                Cursor c = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Equip.class), null, "ido=?",
                        new String[]{getId()},null, null, null);

                if (c.moveToFirst() == false) {
                    item.state = ScriptItem.DOC_SKIPPED;
                    write();
                    close();
                }

                c.close();
            }
        }

        return result;
    }

    public List<DocExportListener> getSendedDocuments() {
        List<DocExportListener> docs = new ArrayList<DocExportListener>();

        docs.add(new DocSendListner(ScriptDoc.OBJ_NAME, this));

        int index = 0;
        CreatableDocument<?>[] cd = getDocuments();
        for( ScriptItem si : data.items ) {
            if( cd[index] != null) {
                docs.add(new DocSendListner(si.type, cd[index]));

                if (si.type.equals(InvEquDoc.OBJ_NAME)) {
                    InvEquImpl invEqu = (InvEquImpl) cd[index];

                    VisitImpl refVisit = new VisitImpl();

                    if (refVisit.read(invEqu.getData().visitDoc.getTime()))
                        docs.add(new DocSendListner(VisitDoc.OBJ_NAME, refVisit));

                    refVisit.close();
                }
            }

            index++;
        }

        return docs;
    }
}
