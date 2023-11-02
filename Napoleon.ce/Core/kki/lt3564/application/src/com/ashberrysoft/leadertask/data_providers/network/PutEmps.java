package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

/**
 * @since 2014-06-19
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutEmps extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutEmps";
    private static final String LIST_ENTITIES = "emps";

    public PutEmps(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        final Cursor s = mContext.getContentResolver().query(EmpContract.CONTENT_URI, null,
                EmpContract.selectionSendEntity(true), null, null);

        writer.write(getOpen(LIST_ENTITIES));
        if (s.getCount() > 0) {

            final StringBuilder sb = new StringBuilder();
            final Emp emp = new Emp();
            for (s.moveToFirst(); !s.isAfterLast(); s.moveToNext()) {
                emp.setData(s);
                Utils.clearStringBuilder(sb);
                emp.getLionEntity(sb);

                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
        s.close();

        {
            final ContentValues cv = new ContentValues(1);
            cv.put(EmpContract.SEND_ENTITY, 0);
            mContext.getContentResolver().update(EmpContract.CONTENT_URI, cv,//
                    EmpContract.selectionSendEntity(true), null);
        }
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<Emp>> handler = SwitchParseHandler.newInstance(inputStream);
        final BaseLionPutEntity<Emp> entity = handler.getData();

        if (!entity.getListDelete().isEmpty()) {
            mContext.getContentResolver().delete(EmpContract.CONTENT_URI,
                    SelectionKeeper.in(null, EmpContract.UID, entity.getListDelete()), null);

            Emp.updateTaskUserOrderAfterDelete(mContext, entity.getListDelete());
        }

        Emp.addOrUpdateEntity(mContext, entity.getListChange());
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_EMPS;
    }
}