package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

public class PutContacts extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutContacts";
    private static final String LIST_ENTITIES = "contacts";

    private List<Contact> mEntities;

    public PutContacts(Context context, LeaderTaskUser user, List<Contact> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (mEntities != null && !mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (Contact entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<Contact>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionPutEntity<Contact> entity = handler.getData();
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        if (!entity.getListDelete().isEmpty()) {
            UidToDelete.removeUidsFromTable(mContext, entity.getListDelete(), ContactContract.SERVER_CLASS);
            dbHelper.deleteContacts(convertStringsToUUIDs(entity.getListDelete()));
        }

        if (!entity.getListChange().isEmpty()) {
            dbHelper.updateContacts(entity.getListChange());
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_CONTACTS;
    }
}