package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.views.ContactsGroupListItem;
import com.ashberrysoft.leadertask.views.ContactsGroupListItem.OnContactsGroupListItemListener;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.adapters.TreeAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

public class ContactsGroupAdapter extends TreeAdapter implements OnContactsGroupListItemListener {

    static CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>> sFactory = new CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>>() {
        @Override
        public IDataView<ITreePureNode> createView(Context context, int type) {
            return new ContactsGroupListItem(context);
        }
    };

    // VALUE's
    private ContactsGroup mSelectedContactsGroups;
    private DbHelper mDbHelper;

    // private boolean mIsCollapseExpand = true;

    public ContactsGroupAdapter(Context context, ITreeData<?> root, ContactsGroup selectedContactsGroups) {
        super(context, root, sFactory);

        mDbHelper = DbHelper.getInstance(mContext);

        if (selectedContactsGroups == null) {
            selectedContactsGroups = new ContactsGroup();
        }
        mSelectedContactsGroups = selectedContactsGroups;
    }

    @Override
    public int getViewTypeCount() {
        return ETreeDataNodeLevel.CONTACTS_GROUP.ordinal() + 1;
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final ContactsGroupListItem v = cV == null ? new ContactsGroupListItem(mContext, this) : (ContactsGroupListItem) cV;
        final ITreePureNode item = (ITreePureNode) getItem(position);
        v.setData(item);
        v.setChecked(mSelectedContactsGroups);
        return v;
    }

    /**
     * Return selected categories set.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @return selected categories set.
     */
    public ContactsGroup getSelectedContactsGroup() {
        return mSelectedContactsGroups;
    }

    @Override
    public void onContactsGroupChecked(ContactsGroup contactsGroup, boolean isChecked) {
        if (isChecked) {
            mSelectedContactsGroups = contactsGroup;
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onContactsGroupOpen(ContactsGroup contactsGroup, boolean isCollapsed) {
        mDbHelper.setContactsGroupCollapsed(contactsGroup, !contactsGroup.isCollapsed());
        notifyDataSetChanged();
    }

}
