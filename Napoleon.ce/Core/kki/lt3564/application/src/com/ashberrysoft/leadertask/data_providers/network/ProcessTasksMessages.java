package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessTasksMessages extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessTasksMessages";

    private List<TaskMessage> mListVerify;
    private List<UUID> mListSend;

    public ProcessTasksMessages(Context context, LeaderTaskUser user, List<TaskMessage> verify) {
        super(context, METHOD_NAME, user);
        mListVerify = verify;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (mListVerify != null && !mListVerify.isEmpty()) {
            for (TaskMessage messages : mListVerify) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                writer.write(String.valueOf(messages.getId()));
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(String.valueOf(messages.getUsn()));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));

        writer.write(getOpen(OBJECTS_TO_REMOVE));
        // TODO: may be must remove
        writer.write(getClose(OBJECTS_TO_REMOVE));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        final Dao<TaskMessage, UUID> dao = dbHelper.getTaskMessageDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);

        try {

            /*final SwitchParseHandler<BaseLionProcessEntity<TaskMessage>> handler = SwitchParseHandler
                    .newInstance(inputStream);
            final BaseLionProcessEntity<TaskMessage> entity = handler.getData();

            boolean calculate = false;

            if (!entity.getListDelete().isEmpty()) {
                dbHelper.deleteTaskMessages(convertStringsToUUIDs(entity.getListDelete()));
                calculate = true;
            }

            mListSend = convertStringsToUUIDs(entity.getListSend());

            if (!entity.getListProcess().isEmpty()) {
                dbHelper.deleteTaskMessages(convertStringsToUUIDs(entity.getListProcess()));
                calculate = true;
            }

            if (!entity.getListAdd().isEmpty()) {
                dbHelper.updateTaskMessages(entity.getListAdd());
                calculate = true;
            }

            if (calculate) {
                dbHelper.calculateTaskMessagesInTask(mContext);
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
        return ServiceConstants.ACTION_PROCESS_TASKS_MESSAGES;
    }

    public List<UUID> getListSend() {
        return mListSend;
    }

    public void clearListSend() {
        mListSend.clear();
        mListSend = null;
    }
}