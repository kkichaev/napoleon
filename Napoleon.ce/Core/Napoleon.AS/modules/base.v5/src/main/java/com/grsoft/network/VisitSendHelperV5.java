package com.grsoft.network;

import android.content.Context;

import com.grsoft.database.DataObjectSendHitching;
import com.grsoft.database.DbWriter;
import com.grsoft.database.VisitItemHitching;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PhotoListDoc;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;

import java.util.ArrayList;
import java.util.List;

public class VisitSendHelperV5 {
    int traffic = 0;
    String error = null;

    public int getTraffic() { return traffic; }
    public String getError() { return error; }

    public boolean send(Context context, UserInfo userInfo, List<CreateDocDataObject> docs, UpdateProcessListener listener) {
        boolean ret = true;

        if(docs.size() == 0)
            return true;

        ConnectionHelper.Result cres = ConnectionHelper.getConnection(userInfo);
        if(cres.error != null) {
            error = cres.error;
            return false;
        }

        DbWriter w = new DbWriter();

        List<ObjectExportListener> toSend = new ArrayList<ObjectExportListener>();

        traffic = 0;

        int count = 0;
        for(CreateDocDataObject doc : docs)
            count += ((PhotoListDoc)doc).getItems().size();

        if(listener != null)
            listener.onUpdate(UpdateProcessInfo.UpdateStatus.BEGIN_SEND_VISITS, count);

        count = 0;

        WriteServiceBase writeService = RWServiceFactory.instance.createWriteService(null);
        for(CreateDocDataObject doc : docs) {
            PhotoListDoc pd = (PhotoListDoc)doc;

            DataObjectSendHitching vhs = new DataObjectSendHitching(doc, pd.getDocName());
            toSend.add(vhs);

            List<VisitItem> items = pd.getItems();
            pd.setItems(new ArrayList<VisitItem>());

            int idx = 0;
            do {
                if(idx < items.size())
                    toSend.add(new VisitItemHitching((CreateDocDataObject)doc, idx, items, pd.getItemName()));

                if(!((WriteServiceV5)writeService).sendPhotos(context, userInfo, toSend, cres.connection, false)) {
                    ret = false;
                    error = writeService.getMessage();
                    break;
                }

                if(listener != null)
                    listener.onUpdate(UpdateProcessInfo.UpdateStatus.STEP, ++count);

                toSend.clear();
                idx++;
            } while(idx < items.size());

            if(!ret)
                break;
            doc.params |= ParamState.ofExported;
            pd.setItems(items);
            w.insertRecord(doc);
        }
        traffic += writeService.getSendedBytes();
        if(listener != null)
            listener.onUpdate(UpdateProcessInfo.UpdateStatus.END, ++count);

        w.close();
        try {
            writeService.sendByeCommanToCloseSession(userInfo, cres.connection, context);
            cres.connection.close();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        writeService.closeConnection();
        return ret;
    }
}
