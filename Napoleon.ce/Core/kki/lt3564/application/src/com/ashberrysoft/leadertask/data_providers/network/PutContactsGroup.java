package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutContactsGroup extends BaseTimeSOAP<Serializable> {

    private static final long serialVersionUID = 1L;
    private static final String METHOD_NAME = "PutContactGroups";
    private static final String LIST_ENTITIES = "contact_groups";

    private List<ContactsGroup> mEntities;

    public PutContactsGroup(Context context, LeaderTaskUser user, List<ContactsGroup> entities) {
        super(context, METHOD_NAME, user);
        mEntities = entities;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write(getOpen(LIST_ENTITIES));
        if (mEntities != null && !mEntities.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (ContactsGroup entity : mEntities) {
                clearStringBuilder(sb);
                entity.getLionEntity(sb);
                writer.write(sb.toString());
            }
        }
        writer.write(getClose(LIST_ENTITIES));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
        /*final SwitchParseHandler<BaseLionPutEntity<ContactsGroup>> handler = SwitchParseHandler.newInstance(inputStream);

        final BaseLionPutEntity<ContactsGroup> entity = handler.getData();
        final DbHelper dbHelper = DbHelper.getInstance(mContext);

        if (!entity.getListDelete().isEmpty()) {
            UidToDelete.removeUidsFromTable(mContext, entity.getListDelete(), ContactsGroup.SERVER_CLASS);
            dbHelper.deleteContactsGroups(convertStringsToUUIDs(entity.getListDelete()));
        }

        if (!entity.getListChange().isEmpty()) {
            dbHelper.updateContactsGroups(entity.getListChange());
        }
*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PUT_CONTACTS_GROUPS;
    }
}