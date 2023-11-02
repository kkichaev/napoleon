package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutContactsFiles extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutContactsFiles";
    private static final String LIST_ENTITIES = "contactsfiles";

    public PutContactsFiles(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        final Cursor s = mContext.getContentResolver().query(ContactsFileContract.CONTENT_URI, null,
                ContactsFileContract.selectionSendFile(true), null, null);

        writer.write(getOpen(LIST_ENTITIES));
        if (s.getCount() > 0) {
            final StringBuilder sb = new StringBuilder();
            final ContactFile ContactFile = new ContactFile();

            for (s.moveToFirst(); !s.isAfterLast(); s.moveToNext()) {
                ContactFile.setData(s);
                Utils.clearStringBuilder(sb);
                ContactFile.getLionEntity(sb);

                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
        s.close();

        {
            final ContentValues cv = new ContentValues(1);
            cv.put(ContactsFileContract.SEND_FILE, 0);
            mContext.getContentResolver().update(ContactsFileContract.CONTENT_URI, cv,
                    ContactsFileContract.selectionSendFile(true), null);
        }
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*SwitchParseHandler<BaseLionPutEntity<ContactFile>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionPutEntity<ContactFile> entity = handler.getData();
        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        boolean wasChange = false;
        for (String uid : entity.getListDelete()) {
            deleteContactFileAndFile(app, cr, uid);
            wasChange = true;
        }

        if (ProcessContactFiles.addOrUpdateContactFiles(app.getAppFolder(), cr, entity.getListChange())) {
            wasChange = true;
        }

        if (wasChange) {
            //DbHelper.calculateFilesInContact(app);

        }
*/
        return null;
    }

    private void deleteContactFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionFieldUid(uid), null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(ContactsFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(ContactsFileContract.CONTENT_URI, ContactsFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, ContactsFileContract.selectionFieldUid(uid));
                }
            }
        }
        catch (Exception e)
        {
            Utils.toLog(e);
        }
        finally {
            if(c!= null)
            {
                c.close();
            }
        }
    }

    private void deleteContact(ContentResolver cr, String uid) {

        String where = uid;
        String[] params = new String[]{};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(ContactsFileContract.CONTENT_URI)
                .withSelection(where, params)
                .build());
        try {
            cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_CONTACTS_FILES;
    }
}