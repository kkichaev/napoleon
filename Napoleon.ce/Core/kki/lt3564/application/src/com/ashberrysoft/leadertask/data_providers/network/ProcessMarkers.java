package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.modern.cache.MarkerCache;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessMarkers extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessMarkers";

    private List<Marker> mListVerify;
    private List<UUID> mListSend;

    public ProcessMarkers(Context context, LeaderTaskUser user, List<Marker> verify) {
        super(context, METHOD_NAME, user);
        mListVerify = verify;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (mListVerify != null && !mListVerify.isEmpty()) {
            for (Marker marker : mListVerify) {
                if (Marker.DEFAULT_MARKER_UUID.equals(marker.getId())) {
                    continue;
                }

                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                writer.write(marker.getId().toString());
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(String.valueOf(marker.getUsn()));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));

        writer.write(getOpen(OBJECTS_TO_REMOVE));
        final Cursor r = mContext.getContentResolver().query(UidToDeleteContract.CONTENT_URI, null,
                UidToDeleteContract.selectionServerClass(Marker.SERVER_CLASS), null, null);
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
        final Dao<Marker, UUID> dao = dbHelper.getMarkerDao();
        final DatabaseConnection connection = dao.startThreadConnection();
        dao.setAutoCommit(connection, false);

        try {

            final SwitchParseHandler<BaseLionProcessEntity<Marker>> handler = SwitchParseHandler
                    .newInstance(inputStream);
            final BaseLionProcessEntity<Marker> entity = handler.getData();
            List <Marker> markers = new ArrayList<>();
            for (Marker marker : entity.getListAdd()) {
                if (!Marker.DEFAULT_MARKER_UUID.equals(marker.getId())) {
                    markers.add(marker);
                }
            }
            entity.getListAdd().clear();
            entity.setListAdd(markers);

            if (!entity.getListDelete().isEmpty()) {
                for(String uidToDelete: entity.getListDelete()) {
                    // обновляем маркера и порядки задач с удаляемым маркером на дефотный
                    Marker.updateTaskMarkerOrder(uidToDelete.toUpperCase(), 0, mContext);
                    Marker tempMarker = dbHelper.getMarkerDao().queryForId(UUID.fromString(uidToDelete)); //ищем в кеше
                    MarkerCache.getInstance(mContext).remove(tempMarker); // удаляем с кеша
                }
                dbHelper.deleteMarkers(convertStringsToUUIDs(entity.getListDelete()));
            }

            mListSend = convertStringsToUUIDs(entity.getListSend());

            if (!entity.getListProcess().isEmpty()) {
                dbHelper.deleteMarkers(convertStringsToUUIDs(entity.getListProcess()));
            }

            if (!entity.getListAdd().isEmpty()) {
                dbHelper.updateMarkers(entity.getListAdd());
            }

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
        return ServiceConstants.ACTION_PROCESS_MARKERS;
    }

    public List<UUID> getListSend() {
        return mListSend;
    }

    public void clearListSend() {
        mListSend.clear();
        mListSend = null;
    }
}