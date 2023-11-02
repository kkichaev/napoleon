package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutProjects extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutProjects";
    private static final String LIST_ENTITIES = "projects";

    private List<Project> mEntities;

    public PutProjects(Context context, LeaderTaskUser user, List<Project> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (mEntities != null && !mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Project entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<Project>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionPutEntity<Project> entity = handler.getData();
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        if (!entity.getListDelete().isEmpty()) {
            UidToDelete.removeUidsFromTable(mContext, entity.getListDelete(), Project.SERVER_CLASS);
            dbHelper.deleteProjects(convertStringsToUUIDs(entity.getListDelete()));
        }

        if (!entity.getListChange().isEmpty()) {
            dbHelper.updateProjects(entity.getListChange());
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_PROJECTS;
    }
}