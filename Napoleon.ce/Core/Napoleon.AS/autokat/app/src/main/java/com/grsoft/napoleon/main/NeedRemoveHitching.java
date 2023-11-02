package com.grsoft.napoleon.main;

import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.NeedRemove;
import com.grsoft.dataobjects.Purchase;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.Selling;
import com.grsoft.dataobjects.impl.AnswerImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.dataobjects.impl.SellingImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.SellingDoc;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.script.documents.ScriptDoc;

public class NeedRemoveHitching extends Hitching {
    public NeedRemoveHitching() {
        super(NeedRemove.class);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        NeedRemove dobj = rawObject.createDataObject(dataObject);

        if (dobj.id.trim().length() > 0)
            removeClient(dobj.id);
        else
            removeDocument(dobj);
    }

    private void removeDocument(NeedRemove dobj) {
        ScriptImpl script = new ScriptImplEx();
        script.getData().created = dobj.docCreated;

        if (script.read())
            script.delete();

        script.close();
    }

    private void removeClient(String param) {
        String[] client = param.split("\t");

        if (client.length == 3){
            String id = client[0];
            String fio = client[1];
            String phone = client[2];

            String where = String.format("fio='%s' and phone = '%s'", fio, phone);
            DocList docs = ScriptDoc.instance().docList(id, null, where);

            for (Document d : docs)
            {
                ScriptImpl script = (ScriptImpl) d;

                VisitImpl visit = new VisitImpl();
                visit.read(((ScriptEx)script.getData()).visitDoc.getTime());
                visit.delete();
                visit.close();

                script.delete();
                script.close();
            }
        }
    }
}
