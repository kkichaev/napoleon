package com.ashberrysoft.leadertask.data_providers.network;

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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * 
 * @since 2014-06-19
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessEmps extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessEmps";

    public ProcessEmps(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {

        final ContentResolver cr = mContext.getContentResolver();

        final Cursor v = cr.query(EmpContract.CONTENT_URI, null, null, null, null);
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(EmpContract.UID);
            final int usn = v.getColumnIndex(EmpContract.USN_ENTITY);

            for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                final String stringUUID = v.getString(uid);
                writer.write(stringUUID.equals(Emp.DEFAULT_UUID_EMP_S) ? Emp.DEFAULT_STRING_EMP : stringUUID);
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(v.getString(usn));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));
        v.close();

        final Cursor r = cr.query(UidToDeleteContract.CONTENT_URI, null,
                UidToDeleteContract.selectionServerClass(EmpContract.SERVER_CLASS), null, null);
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        if (r.getCount() > 0) {
            final int uid = r.getColumnIndex(UidToDeleteContract.UID);

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
        /*final SwitchParseHandler<BaseLionProcessEntity<Emp>> handler = SwitchParseHandler.newInstance(inputStream);
        final BaseLionProcessEntity<Emp> entity = handler.getData();

        boolean wasChanged = false;

        if (!entity.getListDelete().isEmpty()) {
            mContext.getContentResolver().delete(EmpContract.CONTENT_URI,
                    SelectionKeeper.inToLowerCase(null, EmpContract.UID, entity.getListDelete()), null);

            Emp.updateTaskUserOrderAfterDelete(mContext, entity.getListDelete());
            wasChanged = true;
        }

        if (!entity.getListProcess().isEmpty()) {
            UidToDelete.removeUidsFromTable(mContext, entity.getListProcess(), EmpContract.SERVER_CLASS);
            wasChanged = true;
        }

        if (Emp.addOrUpdateEntity(mContext, entity.getListAdd())) {
            wasChanged = true;
        }

        if (!entity.getListSend().isEmpty()) {
            final ContentValues cv = new ContentValues(1);
            cv.put(EmpContract.SEND_ENTITY, 1);

            for (int i = 0; i < entity.getListSend().size(); i++) {
                if (Emp.DEFAULT_STRING_EMP.equals(entity.getListSend().get(i))) {
                    entity.getListSend().set(i, Emp.DEFAULT_UUID_EMP_S);
                    break;
                }
            }

            mContext.getContentResolver().update(EmpContract.CONTENT_URI, cv,//
                    SelectionKeeper.in(null, EmpContract.UID, entity.getListSend()), null);

            wasChanged = false;
        }

        if (wasChanged) {
            Emp.reSortEmp(mContext);
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_EMPS;
    }

}