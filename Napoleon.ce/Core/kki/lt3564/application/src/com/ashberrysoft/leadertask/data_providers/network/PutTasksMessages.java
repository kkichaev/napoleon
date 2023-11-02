package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutTasksMessages extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutTasksMessages";
    private static final String LIST_ENTITIES = "tasksmsgs";

    private List<TaskMessage> mEntities;

    public PutTasksMessages(Context context, LeaderTaskUser user, List<TaskMessage> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (mEntities != null && !mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (TaskMessage entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);

                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<TaskMessage>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionPutEntity<TaskMessage> entity = handler.getData();
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        boolean calculate = false;

        if (!entity.getListDelete().isEmpty()) {
            dbHelper.deleteTaskMessages(convertStringsToUUIDs(entity.getListDelete()));
            calculate = true;
        }

        if (!entity.getListChange().isEmpty()) {
            dbHelper.updateTaskMessages(entity.getListChange());
            calculate = true;
        }

        if (calculate) {
            dbHelper.calculateTaskMessagesInTask(mContext);
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_TASKS_MESSAGES;
    }
}