package com.ashberrysoft.leadertask.data_providers.network;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import static com.ashberrysoft.leadertask.utils.Utils.toLog;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessTaskFiles extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessTasksFiles";

    public ProcessTaskFiles(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        final ContentResolver cr = mContext.getContentResolver();

        final Cursor v = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionDeleteObject(false),
                null, null);
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(TaskFileContract.FIELD_UID);
            final int usn = v.getColumnIndex(TaskFileContract.FIELD_USN_ENTITY);

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

        final Cursor r = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionDeleteObject(true),
                null, null);
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        if (r.getCount() > 0) {
            final int uid = r.getColumnIndex(TaskFileContract.FIELD_UID);

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
        /*final SwitchParseHandler<BaseLionProcessEntity<TaskFile>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionProcessEntity<TaskFile> entity = handler.getData();

        final ContentResolver cr = mContext.getContentResolver();
        final LTApplication app = (LTApplication) mContext.getApplicationContext();

        boolean wasChange = false;
        for (String uid : entity.getListDelete()) {
            deleteTaskFileAndFile(app, cr, uid);
            wasChange = true;
        }

        for (String uid : entity.getListProcess()) {
            deleteTaskFileAndFile(app, cr, uid);

            wasChange = true;
        }

        if (!entity.getListSend().isEmpty()) {
            final ContentValues cv = new ContentValues(1);
            cv.put(TaskFileContract.SEND_FILE, 1);

            cr.update(TaskFileContract.CONTENT_URI, cv,//
                    SelectionKeeper.in(null, TaskFileContract.FIELD_UID, entity.getListSend()), null);
            wasChange = true;
        }

        if (addOrUpdateTaskFiles(app.getAppFolder(), cr, entity.getListAdd())) {
            wasChange = true;
        }

        if (wasChange) {
            DbHelper.calculateFilesInTask(app); // ne rabotaet

        }
*/
        return null;
    }
    
    private void deleteTaskFileAndFile(LTApplication app, ContentResolver cr, String uid) {
        final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, TaskFileContract.selectionFieldUid(uid), null, null);

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
    }

    public static boolean addOrUpdateTaskFiles(File appFolder, ContentResolver cr, List<TaskFile> files)//
            throws RemoteException, OperationApplicationException {
        if (files.isEmpty()) {
            return false;
        }

        final ArrayList<ContentProviderOperation> oprtns = new ArrayList<ContentProviderOperation>(files.size());
        final StringBuilder sb = new StringBuilder();
        final TaskFile fileOld = new TaskFile();

        for (TaskFile fileNew : files) {
            Utils.clearStringBuilder(sb);
            final String selection = SelectionKeeper.equals(sb, TaskFileContract.FIELD_UID, fileNew.getId());
            final Cursor c = cr.query(TaskFileContract.CONTENT_URI, null, selection, null, null);

            if (c.getCount() == 1 && c.moveToFirst()) {
                fileOld.setData(c);

                final ContentValues cv = new ContentValues();
                cv.put(TaskFileContract.DELETE_OBJECT, 0);

                if (fileOld.getUsnEntity() != fileNew.getUsnEntity()) {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, fileNew.getUsnEntity());
                    cv.put(TaskFileContract.FIELD_UID, fileNew.getId().toString());
                    cv.put(TaskFileContract.FIELD_TASKUID, fileNew.getTaskId().toString());
                    cv.put(TaskFileContract.FIELD_FILEUID, fileNew.getFileId().toString());
                    cv.put(TaskFileContract.FIELD_EMAILCREATOR, fileNew.getEmailCreator());
                }

                if (fileOld.getUsnFieldOrder() <= fileNew.getUsnFieldOrder()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_ORDER, fileNew.getUsnFieldOrder());
                    cv.put(TaskFileContract.ORDERS, fileNew.getOrder());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldName() <= fileNew.getUsnFieldName()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_NAME, fileNew.getUsnFieldName());
                    cv.put(TaskFileContract.FIELD_FILENAME, fileNew.getFileName());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldSize() <= fileNew.getUsnFieldSize()) {
                    cv.put(TaskFileContract.FIELD_USN_FIELD_SIZE, fileNew.getUsnFieldSize());
                    cv.put(TaskFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                } else {
                    cv.put(TaskFileContract.FIELD_USN_ENTITY, 0);
                }

                if (fileOld.getUsnFieldVersion() != fileNew.getUsnFieldVersion()) {
                    cv.put(TaskFileContract.FIELD_FILEVERSION, fileNew.getFileVersion());
                    cv.put(TaskFileContract.FIELD_USN_FIELD_VERSION, fileNew.getUsnFieldVersion());
                    cv.put(TaskFileContract.FIELD_FILESIZE, fileNew.getFileSize());
                    cv.put(TaskFileContract.FILE_EXIST, 0);
                    new File(appFolder, fileOld.getFileName()).delete();
                }

                oprtns.add(ContentProviderOperation.newUpdate(TaskFileContract.CONTENT_URI)//
                        .withSelection(selection, null)//
                        .withValues(cv)//
                        .build());
            } else {
                oprtns.add(ContentProviderOperation.newInsert(TaskFileContract.CONTENT_URI)//
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
        return ServiceConstants.ACTION_PROCESS_TASK_FILES;
    }
}