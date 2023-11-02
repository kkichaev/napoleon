package com.ashberrysoft.leadertask.domains.ordinary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Корневой элемент для дерева категорий.
 *
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 *
 */
public class ContactTreeRoot //
        implements ITreeData<ITreeData<Contact>>, SlidingMenuTreeDataContainer {

    public static final UUID sContactsRootUUID = UUID.fromString("ae0fa485-5981-4742-95a3-48ada7accdd1");
    private Context mContext;
    private List<ITreeData<Contact>> mItems;
    private int mNodeLevel;
    private String mName;
    private boolean mUpdate;

    public ContactTreeRoot(Context context, DbHelper database) {
        mContext = context;
        mNodeLevel = 0;

        try {
            updateContacts(database);

        } catch (SQLException e) {
            mItems = new ArrayList<ITreeData<Contact>>(0);

        } catch (AbstractDataRequestException e) {
            mItems = new ArrayList<ITreeData<Contact>>(0);
        }
    }

    public void updateContacts(DbHelper database) throws SQLException, AbstractDataRequestException {
        final List<Contact> listContacts = database.getAllContacts();
        mItems = new ArrayList<ITreeData<Contact>>();

        for (Contact contact : listContacts) {
            if (contact.getUidParent() == null) {
                contact.setExpanded(contact.isCollapsed());
                mItems.add(contact);
                processContacts(contact, listContacts);
            }
        }
    }

    /**
     * Create tree hierarchy for contacts
     *
     * @param contact
     *            - particulat contact
     * @param contacts
     *            - all contacts
     *
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    private void processContacts(Contact contact, List<Contact> contacts) {
        for (Contact categ : contacts) {
            if (contact.getId().equals(categ.getUidParent())) {// TODO:
                contact.addChild(categ);
                processContacts(categ, contacts);
            }
        }
    }

    @Override
    public int getNodeLevel() {
        return mNodeLevel;
    }

    @Override
    public boolean isExpandable() {
        return true;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

    @Override
    public int getChildsCount() {
        return mItems.size();
    }

    @Override
    public List<ITreeData<Contact>> getSubnodes() {
        return mItems;
    }

    @Override
    public void setExpanded(boolean value) {}

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getIndent() {
        return 0;
    }

    @Override
    public String getFilterId() {
        return sContactsRootUUID.toString();
    }
}
