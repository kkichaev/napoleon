package com.ashberrysoft.leadertask.data_providers.network;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.SimpleProcessHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessFiles extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessFiles";

    public ProcessFiles(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        final ContentResolver cr = mContext.getContentResolver();

        final Cursor v = cr.query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionDeleteObjectFileExist(false, true), null, null);
        final Cursor c = cr.query(ContactsFileContract.CONTENT_URI, null,
                ContactsFileContract.selectionDeleteObjectFileExist(false, true), null, null);
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(TaskFileContract.FIELD_FILEUID);
            final int usn = v.getColumnIndex(TaskFileContract.FIELD_FILEVERSION);

            for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));
                writer.write(getOpen(_STR_UID));
                writer.write(v.getString(uid));
                writer.write(getClose(_STR_UID));
                writer.write(getOpen(_USN_ENTITY));
                writer.write(v.getString(usn));
                writer.write(getClose(_USN_ENTITY));
                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }

        if (c.getCount() > 0) {
            final int uid = c.getColumnIndex(ContactsFileContract.FIELD_FILEUID);
            final int usn = c.getColumnIndex(ContactsFileContract.FIELD_FILEVERSION);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));
                writer.write(getOpen(_STR_UID));
                writer.write(c.getString(uid));
                writer.write(getClose(_STR_UID));
                writer.write(getOpen(_USN_ENTITY));
                writer.write(c.getString(usn));
                writer.write(getClose(_USN_ENTITY));
                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));
        c.close();
        v.close();

        final Cursor r = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionDeleteObject(true),
                null, null);
        final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionDeleteObject(true),
                null, null);
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        if (r.getCount() > 0) {
            final int uid = r.getColumnIndex(TaskFileContract.FIELD_FILEUID);

            for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));
                writer.write(getOpen(_STR_UID));
                writer.write(r.getString(uid));
                writer.write(getClose(_STR_UID));
                writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
            }
        }

        if (cursor.getCount() > 0) {
            final int uid = cursor.getColumnIndex(ContactsFileContract.FIELD_FILEUID);

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));
                writer.write(getOpen(_STR_UID));
                writer.write(cursor.getString(uid));
                writer.write(getClose(_STR_UID));
                writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
            }
        }
        writer.write(getClose(OBJECTS_TO_REMOVE));
        r.close();
        cursor.close();
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
       /* final SwitchParseHandler<SimpleProcessEntity> handler = SwitchParseHandler.newInstance(inputStream);
        final SimpleProcessEntity entity = handler.getData();

        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        for (String uid : entity.getListDelete()) {
            deleteTaskFileAndFile(app, cr, uid);
        }

        final ContentValues cv = new ContentValues();
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sbContacts = new StringBuilder();
        for (String uid : entity.getListSend()) {
            final String where = TaskFileContract.selectionFieldFileUid(uid);
            final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, where, null, null);
            c.moveToFirst();
            if (c != null && c.getCount() > 0) {
                TaskFile taskFile = new TaskFile(c);
                UploadFile uploadFile = new UploadFile(mContext, app.getSettings().getUserProfile(), app.getAppFolder(), sb, taskFile.getFileName(), taskFile.getFileId().toString(), taskFile.getEmailCreator(), taskFile.getFileVersion(), 0);
                final Integer usn = uploadFile.uploadFile();
                sb.append(SharedStrings.NEW_LINE_C);

                if (usn != null) {
                    cv.clear();
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                    cv.put(TaskFileContract.FIELD_FILEVERSION, usn);
                    cr.update(TaskFileContract.CONTENT_URI, cv, where, null);
                }
            }
            c.close();
        }
        saveResponse(mContext, new ByteArrayInputStream(sb.toString().getBytes()), UploadFile.METHOD_TYPE);

        for (String uid : entity.getListSend()) {
            final String where = ContactsFileContract.selectionFieldFileUid(uid);
            final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, where, null, null);
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount() > 0) {
                ContactFile taskFile = new ContactFile(cursor);
                UploadFile uploadFile = new UploadFile(mContext, app.getSettings().getUserProfile(), app.getAppFolder(), sbContacts, taskFile.getFileName(), taskFile.getFileId().toString(), taskFile.getEmailCreator(), taskFile.getFileVersion(), 0);
                final Integer usn = uploadFile.uploadFile();
                sbContacts.append(SharedStrings.NEW_LINE_C);

                if (usn != null) {
                    cv.clear();
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                    cv.put(ContactsFileContract.FIELD_FILEVERSION, usn);
                    cr.update(ContactsFileContract.CONTENT_URI, cv, where, null);
                }
            }
            cursor.close();
        }
        saveResponse(mContext, new ByteArrayInputStream(sbContacts.toString().getBytes()), UploadFile.METHOD_TYPE);
*/
        return null;
    }

    private void deleteTaskFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionFieldFileUid(uid),
                null, null);

        try {
            while (c.moveToNext()) {
                if (c.isFirst()) {
                    new File(app.getAppFolder(), c.getString(c.getColumnIndex(TaskFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, TaskFileContract.selectionFieldUid(uid));
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

        final Cursor cursor = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionFieldFileUid(uid),
                null, null);

        try {
            while (cursor.moveToNext()) {
                if (cursor.isFirst()) {
                    new File(app.getAppFolder(), cursor.getString(cursor.getColumnIndex(ContactsFileContract.FIELD_FILENAME))).delete();
                    //cr.delete(TaskFileContract.CONTENT_URI, TaskFileContract.selectionFieldUid(uid), null);
                    deleteContact(cr, ContactsFileContract.selectionFieldUid(uid));
                }
            }
        }
        catch (Exception e)
        {
            Utils.toLog(e);
        }
        finally {
            if(cursor!= null)
            {
                cursor.close();
            }
        }
    }

    private void deleteContact(ContentResolver cr, String uid) {

        String where = uid;
        String[] params = new String[]{};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(TaskFileContract.CONTENT_URI)
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
        return ServiceConstants.ACTION_PROCESS_FILES;
    }
}