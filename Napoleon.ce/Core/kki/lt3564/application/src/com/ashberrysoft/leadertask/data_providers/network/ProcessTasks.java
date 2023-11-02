package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.CreateOrUpdateTasks;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.DeletedTask;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
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

public class ProcessTasks extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessTasks";

    private List<Task> mListVerify;
    private List<DeletedTask> mListDelete;
    private List<UUID> mListSend;

    public ProcessTasks(Context context, LeaderTaskUser user, List<Task> verify, List<DeletedTask> delete) {
        super(context, METHOD_NAME, user);

        mListVerify = verify;
        mListDelete = delete;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (mListVerify != null && !mListVerify.isEmpty()) {
            for (Task task : mListVerify) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                writer.write(task.getId().toString());
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(String.valueOf(task.getUsn()));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));

        writer.write(getOpen(OBJECTS_TO_REMOVE));
        if (mListDelete != null && !mListDelete.isEmpty()) {
            for (DeletedTask task : mListDelete) {
                writer.write(getOpen(OBJ_CLIENT_TO_REMOVE));

                writer.write(getOpen(_STR_UID));
                writer.write(String.valueOf(task.getId()));
                writer.write(getClose(_STR_UID));

                writer.write(getClose(OBJ_CLIENT_TO_REMOVE));
            }
        }
        writer.write(getClose(OBJECTS_TO_REMOVE));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        final Dao<Task, UUID> dao = dbHelper.getTaskDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);

        try {

            // final ProcessEntityHolder<LTask> entity = PremierEntityParser.parse(inputStream);
            //
            // int count = 0;
            // final StringBuilder sb = new StringBuilder();
            // for (LTask task : entity.getAdd()) {
            // task.getLionEntity(sb);
            //
            // if (count++ > 200) {
            // break;
            // }
            // }
            //
            // LionEntityGenerator.writeToFile(new File("sdcard/ProcessEntityHolder.txt"), sb.toString());

            /*final SwitchParseHandler<BaseLionProcessEntity<Task>> handler = SwitchParseHandler.newInstance(inputStream);
            final BaseLionProcessEntity<Task> entity = handler.getData();

            if (!entity.getListDelete().isEmpty()) {
                dbHelper.deleteTasks(convertStringsToUUIDs(entity.getListDelete()));
            }

            mListSend = convertStringsToUUIDs(entity.getListSend());

            if (!entity.getListProcess().isEmpty()) {
                dbHelper.deleteTasksFromDeletedTaskTable(convertStringsToUUIDs(entity.getListProcess()));
            }

            if (!entity.getListAdd().isEmpty()) {
                final boolean firstSync = dbHelper.getTaskDao_countAll() == 0;
                if (firstSync) {
                    dbHelper.calculateFirstTasksSubtasks(mContext, entity.getListAdd());
                }

                new CreateOrUpdateTasks(mContext, entity.getListAdd(), true).start();

                if (!firstSync) {
                    calculateVerticalOfTaskList(mContext, mUser.getName(), entity.getListAdd());
                }
            }*/

        } finally {
            if (connection != null) {
                dao.commit(connection);
                dao.setAutoCommit(connection, true);
                dao.endThreadConnection(connection);
            }
        }
        return null;
    }

    public static void calculateVerticalOfTaskList(Context context, String currentUser, List<Task> tasks) {
        final List<String> uuids = new ArrayList<String>(tasks.size());
        for (Task task : tasks) {
            uuids.add(String.valueOf(task.getId()));
        }
        tasks.clear();

        DbHelper.getInstance(context).calculateVerticalTasksSubtasks(context, currentUser, uuids);
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_TASKS;
    }

    public List<UUID> getListSend() {
        return mListSend;
    }

    public void clearListSend() {
        mListSend.clear();
        mListSend = null;
    }
}