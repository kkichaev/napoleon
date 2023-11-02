package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessContacts extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessContacts";

    private List<Contact> mListVerify;
    private List<UUID> mListSend;

    public ProcessContacts(Context context, LeaderTaskUser user, List<Contact> verify) {
        super(context, METHOD_NAME, user);
        mListVerify = verify;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (mListVerify != null && !mListVerify.isEmpty()) {
            for (Contact entity : mListVerify) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                writer.write(entity.getId().toString());
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(String.valueOf(entity.getUsn()));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));

        writer.write(getOpen(OBJECTS_TO_REMOVE));
        final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                UidToDeleteContract.selectionServerClass(ContactContract.SERVER_CLASS), null, null);
        if (r.getCount() > 0) {
            final int columnUid = r.getColumnIndex(UidToDeleteContract.UID);
            for (r.moveToFirst(); !r.isAfterLast(); r.moveToNext()) {
                writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                writer.write(getOpen(_STR_UID));
                writer.write(r.getString(columnUid));
                writer.write(getClose(_STR_UID));

                writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
            }
        }
        r.close();
        writer.write(getClose(OBJECTS_TO_REMOVE));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        final Dao<Contact, UUID> dao = dbHelper.getContactDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);

        try {
            
            /*final SwitchParseHandler<BaseLionProcessEntity<Contact>> handler = SwitchParseHandler
                    .newInstance(inputStream);
            final BaseLionProcessEntity<Contact> entity = handler.getData();

            if (!entity.getListDelete().isEmpty()) {
                mContext.getContentResolver().delete(ContactContract.CONTENT_URI,
                        SelectionKeeper.inToLowerCase(null, ContactContract.UID, entity.getListDelete()), null);
                mContext.getContentResolver().notifyChange(ContactContract.CONTENT_URI, null);

            }

            mListSend = convertStringsToUUIDs(entity.getListSend());

            if (!entity.getListProcess().isEmpty()) {
                UidToDelete.removeUidsFromTable(mContext, entity.getListProcess(), ContactContract.SERVER_CLASS);
                dbHelper.deleteContacts(convertStringsToUUIDs(entity.getListProcess()));
            }

            if (!entity.getListAdd().isEmpty()) {
                dbHelper.updateContacts(entity.getListAdd());
            }
            */
        } finally {
            if (connection != null) {
                dao.commit(connection);
                dao.setAutoCommit(connection, true);
                dao.endThreadConnection(connection);
            }
        }

        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_CONTACTS;
    }

    public List<UUID> getListSend() {
        return mListSend;
    }

    public void clearListSend() {
        mListSend.clear();
        mListSend = null;
    }
}