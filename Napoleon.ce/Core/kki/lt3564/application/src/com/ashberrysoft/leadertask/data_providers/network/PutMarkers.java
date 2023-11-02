package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutMarkers extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutMarkers";
    private static final String LIST_ENTITIES = "markers";

    private List<Marker> mEntities;

    public PutMarkers(Context context, LeaderTaskUser user, List<Marker> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (mEntities != null && !mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Marker entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<Marker>> handler = SwitchParseHandler.newInstance(inputStream);
        final BaseLionPutEntity<Marker> entity = handler.getData();
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        if (!entity.getListDelete().isEmpty()) {
            dbHelper.deleteMarkers(convertStringsToUUIDs(entity.getListDelete()));
        }

        if (!entity.getListChange().isEmpty()) {
            dbHelper.updateMarkers(entity.getListChange());
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_MARKERS;
    }
}