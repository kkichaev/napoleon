package com.grsoft.database;

import android.content.Context;

import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DocsToSign;
import com.grsoft.dataobjects.DocsToSignItem;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.io.File;
import java.io.FileOutputStream;

public class DocsToSignReceiver extends RcvNewHitching {
    File dir;
    public DocsToSignReceiver(Context context) {
        super(DocsToSign.class, "NewDocsToSign");

        dir = context.getExternalFilesDir(null);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        DocsToSign ds = rawObject.createDataObject(dataObject);
        for(DocsToSignItem i : ds.documents) {
            if(i.document != null && i.document.length > 0) {
                File file = new File(dir, i.name);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(i.document);
                    fos.close();
                    i.file = file.getAbsolutePath();
                } catch (Exception e) {

                }
            }
        }
        dbProxy.insertRecord(ds);
    }
}
