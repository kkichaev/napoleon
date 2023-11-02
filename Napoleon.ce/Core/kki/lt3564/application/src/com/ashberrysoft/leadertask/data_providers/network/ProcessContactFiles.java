package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessContactFiles extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessContactsFiles";

    public ProcessContactFiles(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        final ContentResolver cr = mContext.getContentResolver();

        final Cursor v = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionDeleteObject(false),
                null, null);
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(ContactsFileContract.FIELD_UID);
            final int usn = v.getColumnIndex(ContactsFileContract.FIELD_USN_ENTITY);

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
        writer.write(getClose(OBJECTS_TO_VERIFY));
        v.close();

        final Cursor r = cr.query(ContactsFileContract.CONTENT_URI, null, ContactsFileContract.selectionDeleteObject(true),
                null, null);
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        if (r.getCount() > 0) {
            final int uid = r.getColumnIndex(ContactsFileContract.FIELD_UID);

            for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                writer.write(getOpen(_STR_UID));
                writer.write(r.getString(uid));
                writer.write(getClose(_STR_UID));

                writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
            }
        }
        writer.write(getClose(OBJECTS_TO_REMOVE));
        r.close();
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final SwitchParseHandler<BaseLionProcessEntity<ContactFile>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionProcessEntity<ContactFile> entity = handler.getData();

        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        boolean wasChange = false;
        for (String uid : entity.getListDelete()) {
            deleteContactFileAndFile(app, cr, uid);
            wasChange = true;
        }

        for (String uid : entity.getListProcess()) {
            deleteContactFileAndFile(app, cr, uid);

            wasChange = true;
        }

        if (!entity.getListSend().isEmpty()) {
            final ContentValues cv = new ContentValues(1);
            cv.put(ContactsFileContract.SEND_FILE, 1);

            cr.update(ContactsFileContract.CONTENT_URI, cv,//
                    SelectionKeeper.in(null, ContactsFileContract.FIELD_UID, entity.getListSend()), null);
            wasChange = true;
        }

        if (addOrUpdateContactFiles(app.getAppFolder(), cr, entity.getListAdd())) {
            wasChange = true;
        }

        if (wasChange) {
            //DbHelper.calculateFilesInContact(app); // ne rabotaet

        }
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

    public static boolean addOrUpdateContactFiles(File appFolder, ContentResolver cr, List<ContactFile> files)//
            throws RemoteException, OperationApplicationException {
        if (files.isEmpty()) {
            return false;
        }

        final ArrayList<ContentProviderOperation> oprtns = new ArrayList<ContentProviderOperation>(files.size());
        final StringBuilder sb = new StringBuilder();
        final ContactFile fileOld = new ContactFile();

        for (ContactFile fileNew : files) {
            Utils.clearStringBuilder(sb);
            final String selection = SelectionKeeper.equals(sb, ContactsFileContract.FIELD_UID, fileNew.getId());
            final Cursor c = cr.query(ContactsFileContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                fileOld.setData(c);

                final ContentValues cv = new ContentValues();
                cv.put(ContactsFileContract.DELETE_OBJECT, 0);

                if (fileOld.getUsnEntity() != fileNew.getUsnEntity()) {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, fileNew.getUsnEntity());
                    cv.put(ContactsFileContract.FIELD_UID, fileNew.getId().toString());
                    cv.put(ContactsFileContract.FIELD_CONTACTUID, fileNew.getContactId().toString());
                    cv.put(ContactsFileContract.FIELD_FILEUID, fileNew.getFileId().toString());
                    cv.put(ContactsFileContract.FIELD_EMAILCREATOR, fileNew.getEmailCreator());
                }

                if (fileOld.getUsnFieldOrder() <= fileNew.getUsnFieldOrder()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_ORDER, fileNew.getUsnFieldOrder());
                    cv.put(ContactsFileContract.ORDERS, fileNew.getOrder());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldName() <= fileNew.getUsnFieldName()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_NAME, fileNew.getUsnFieldName());
                    cv.put(ContactsFileContract.FIELD_FILENAME, fileNew.getFileName());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldSize() <= fileNew.getUsnFieldSize()) {
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_SIZE, fileNew.getUsnFieldSize());
                    cv.put(ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                } else {
                    cv.put(ContactsFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldVersion() != fileNew.getUsnFieldVersion()) {
                    cv.put(ContactsFileContract.FIELD_FILEVERSION, fileNew.getFileVersion());
                    cv.put(ContactsFileContract.FIELD_USN_FIELD_VERSION, fileNew.getUsnFieldVersion());
                    cv.put(ContactsFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                    cv.put(ContactsFileContract.FILE_EXIST, 0);
                    new File(appFolder, fileOld.getFileName()).delete();
                }

                oprtns.add(ContentProviderOperation.newUpdate(ContactsFileContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(ContactsFileContract.CONTENT_URI)//
                        .withValues(fileNew.getContentValues(null))//
                        .build());
            }
            c.close();
        }

        cr.applyBatch(LeaderTaskProviderMetaData.AUTHORITY, oprtns);

        return true;
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
        return ServiceConstants.ACTION_PROCESS_CONTACTS_FILES;
    }
}