package com.ashberrysoft.leadertask.domains.ordinary;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Отображение фильтров
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class ContactsGroupTreeRoot implements ITreeData<ContactsGroup> {
    private SlidingMenuHeader mHeaderProj;

    private List<ContactsGroup> mItems;
    private List<ContactsGroup> mItemsMy;
    private List<ContactsGroup> mItemsShared;

    public ContactsGroupTreeRoot(Context context) {
        mItems = new ArrayList<ContactsGroup>();
        updateContactsGroups(context);
    }

    public void updateContactsGroups(Context context) {

        if (mHeaderProj != null) {
            mItems.remove(mHeaderProj);
        }
        DbHelper dbh = DbHelper.getInstance(context);
        List<ContactsGroup> listContactsGroups = new ArrayList<ContactsGroup>() {};
        List<ContactsGroup> listMyContactsGroups = dbh.getMyContactsGroups();
        List<ContactsGroup> listSharedContactsGroups = dbh.getSharedContactsGroups();

        listContactsGroups.addAll(listMyContactsGroups);
        listContactsGroups.addAll(listSharedContactsGroups);

        for (ContactsGroup contactsGroup : listContactsGroups) {
            if (contactsGroup.getParentId() == null) {
                contactsGroup.setExpanded(contactsGroup.isCollapsed());
                mItems.add(contactsGroup);
                processContactsGroups(contactsGroup, listContactsGroups);
            }
        }
    }

    private void processContactsGroups(ContactsGroup contactsGroup, List<ContactsGroup> contactsGroups) {
        for (ContactsGroup group : contactsGroups)
            if (contactsGroup.getId().equals(group.getParentId())) {
                contactsGroup.addChild(group);
                processContactsGroups(group, contactsGroups);
            }
    }

    @Override
    public int getNodeLevel() {
        return 0;
    }

    @Override
    public boolean isExpandable() {
        return false;
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
    public List<ContactsGroup> getSubnodes() {
        return mItems;
    }

    @Override
    public void setExpanded(boolean value) {
    }
}
