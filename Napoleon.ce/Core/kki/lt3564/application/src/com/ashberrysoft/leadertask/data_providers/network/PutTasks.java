package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.data_providers.CreateOrUpdateTasks;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutTasks extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutTasks";
    private static final String LIST_ENTITIES = "tasks";

    private List<Task> mEntities;

    public PutTasks(Context context, LeaderTaskUser user, List<Task> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (!mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Task entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        final DbHelper dbHelper = DbHelper.getInstance(mContext);
        final Dao<Task, UUID> dao = dbHelper.getTaskDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);

        try {
            /*final SwitchParseHandler<BaseLionPutEntity<Task>> handler = SwitchParseHandler.newInstance(inputStream);
            final BaseLionPutEntity<Task> entity = handler.getData();

            if (!entity.getListDelete().isEmpty()) {
                mContext.getContentResolver().delete(TaskContract.CONTENT_URI,
                        TaskContract.selectionFieldUidInList(entity.getListDelete()), null);
            }

            if (!entity.getListChange().isEmpty()) {
                new CreateOrUpdateTasks(mContext, entity.getListChange(), false).start();
                ProcessTasks.calculateVerticalOfTaskList(mContext, mUser.getName(), entity.getListChange());
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
        return ServiceConstants.ACTION_PUT_TASKS;
    }
}