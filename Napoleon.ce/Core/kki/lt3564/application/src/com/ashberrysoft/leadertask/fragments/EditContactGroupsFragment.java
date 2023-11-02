package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.v2soft.AndLib.dao.ITreePureNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EditContactGroupsFragment extends BaseFeaturesFragment implements OnSimpleFeatureViewListener<ITreePureNode> {

    private static final String CLASS_PATH = EditContactGroupsFragment.class.getName();
    private static final String EXTRA_CONTACT_GROUP = CLASS_PATH + "EXTRA_CONTACT_GROUP";

    // VALUE's
    private MenuInflater mMenuInflater;
    private ContactsGroup mTempContactGroup;
    private ContactsGroup mTempContactGroupPrev;
    private ContactsGroup mTempContactGroupPost;
    private int mTempPosition;

    // ADAPTER
    private SimpleFeatureListAdapter<ITreePureNode> mAdapter;

    public static EditContactGroupsFragment newInstance() {
        return new EditContactGroupsFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mTempContactGroup = b != null ? ((ContactsGroup) b.getSerializable(EXTRA_CONTACT_GROUP)) : null;
        mMenuInflater = getActivity().getMenuInflater();
        mAdapter = new SimpleFeatureListAdapter<ITreePureNode>(getActivity(), FeatureType.CONTACT_GROUPS, this);
        setActionButtonListener();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setData(SimpleFeatureListAdapter.getListContactGroups(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_CONTACT_GROUP, mTempContactGroup);
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    @Override
    public void onSimpleFeatureViewClick(ITreePureNode data) {
        openContactGroup((ContactsGroup) data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, ITreePureNode data, int position, ITreePureNode dataPrev, ITreePureNode dataPost) {
        mTempContactGroup = (ContactsGroup) data;
        mTempContactGroupPrev = (ContactsGroup) dataPrev;
        mTempContactGroupPost = (ContactsGroup) dataPost;
        mTempPosition = position;
        getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempContactGroup == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        if (mTempContactGroup.getParent() == null) {
            setMenuForRoot(menu);
        } else {
            setMenuForChild(menu);
        }

        setMenuForCreator(menu);
    }

    private void setMenuForCreator(ContextMenu menu) {
        if (!mTempContactGroup.getCreator().equals(LTSettings.getInstance().getUserName())) {
            setMenuItemEnabled(menu.findItem(R.id.menu_dell), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }
    }

    private void setMenuForRoot(ContextMenu menu) {
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        if (mTempPosition == 0) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }

        if (mTempPosition == (mAdapter.getData().size() - 1 - recursiveChildsCount(mTempContactGroup))) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }

        if(mTempContactGroupPrev != null) {
            if (mTempContactGroupPrev.getCreator().equals(mSettings.getUserName()) && !mTempContactGroup.getCreator().equals(mSettings.getUserName())) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            }
        }

        if(mTempContactGroupPost != null) {
            if (!mTempContactGroupPost.getCreator().equals(mSettings.getUserName()) && mTempContactGroup.getCreator().equals(mSettings.getUserName())) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
            }
        }
    }

    private void setMenuForChild(ContextMenu menu) {
        final ContactsGroup parent = mTempContactGroup.getParent();
        final List<ContactsGroup> childs = parent.getSubnodes();

        int position;
        for (position = 0; position < childs.size(); position++) {
            if (mTempContactGroup.getId().equals(childs.get(position).getId())) {
                break;
            }
        }

        if (position == 0) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }

        if (position == childs.size() - 1) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_properties:
                openContactGroup(mTempContactGroup);
                return true;

            case R.id.m_go_left:
                setBlockAtUI(true);
                new Thread(mLeftRun).start();
                return true;

            case R.id.m_go_right:
                setBlockAtUI(true);
                new Thread(mRightRun).start();
                return true;

            case R.id.m_go_up:
                setBlockAtUI(true);
                new Thread(mUpRun).start();
                return true;

            case R.id.m_go_down:
                setBlockAtUI(true);
                new Thread(mDownRun).start();
                return true;

            case R.id.menu_dell:
                showSimpleDialog(R.string.d_contact_group_remove_title, R.string.d_contact_group_remove_message);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private final Runnable mLeftRun = new Runnable() {
        @Override
        public void run() {
            goLeft();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private final Runnable mRightRun = new Runnable() {
        @Override
        public void run() {
            goRight();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private final Runnable mUpRun = new Runnable() {
        @Override
        public void run() {
            goUp();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private Runnable mDownRun = new Runnable() {
        @Override
        public void run() {
            goDown();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private void updateAdapterData() {
        mAdapter.setData(SimpleFeatureListAdapter.getListContactGroups(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    private void goLeft() {
        final ContactsGroup parent = mTempContactGroup.getParent();
        final ContactsGroup parentParent = parent.getParent();

        final List<ContactsGroup> contactGroups;
        if (parentParent == null) {
            contactGroups = new ArrayList<ContactsGroup>();
            for (ITreePureNode i : mAdapter.getData()) {
                final ContactsGroup p = (ContactsGroup) i;
                if (p.getParentId() == null) {
                    contactGroups.add(p);
                }
            }
        } else {
            contactGroups = parentParent.getSubnodes();
        }

        int parentPosition = -1;
        for (int i = 0; i < contactGroups.size(); i++) {
            if (contactGroups.get(i).getId().equals(parent.getId())) {
                parentPosition = i;
                break;
            }
        }

        mTempContactGroup.setParentId(parent.getParentId());
        mTempContactGroup.setUsnParent(mTempContactGroup.getUsnParent() + 1);
        contactGroups.add(parentPosition + 1, mTempContactGroup);

        for (int i = 0; i < contactGroups.size(); i++) {
            final ContactsGroup p = contactGroups.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        mDbHelper.updateContactsGroups(contactGroups);
    }

    private void goRight() {
        int newParentPosition = mTempPosition - 1;
        ContactsGroup newParent = null;
        while ((newParent = (ContactsGroup) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContactGroup
                .getIndent()) {
            newParentPosition--;
        }

        final ContactsGroup oldParent = mTempContactGroup.getParent();
        mTempContactGroup.setParentId(newParent.getId());
        mTempContactGroup.setUsnParent(mTempContactGroup.getUsnParent() + 1);

        final List<ContactsGroup> childs = newParent.getSubnodes();
        childs.add(mTempContactGroup);

        for (int i = 0; i < childs.size(); i++) {
            final ContactsGroup p = childs.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        updateOrdersToIndent(oldParent);
        mDbHelper.updateContactsGroups(childs);
    }

    private void goUp() {
        int newParentPosition = mTempPosition - 1;
        ContactsGroup neighbourhood = null;
        while ((neighbourhood = (ContactsGroup) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContactGroup
                .getIndent()) {
            newParentPosition--;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempContactGroup.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

        mTempContactGroup.setOrder(neighbourhoodOrder);
        mTempContactGroup.setUsnPlusPlus();
        mTempContactGroup.setUsnOrder(mTempContactGroup.getUsnOrder() + 1);

        final List<ContactsGroup> contactGroups = new ArrayList<ContactsGroup>(2);
        contactGroups.add(neighbourhood);
        contactGroups.add(mTempContactGroup);

        mDbHelper.updateContactsGroups(contactGroups);
    }

    private void goDown() {
        int newParentPosition = mTempPosition + 1;
        ContactsGroup neighbourhood = null;
        while ((neighbourhood = (ContactsGroup) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContactGroup
                .getIndent()) {
            newParentPosition++;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempContactGroup.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

        mTempContactGroup.setOrder(neighbourhoodOrder);
        mTempContactGroup.setUsnPlusPlus();
        mTempContactGroup.setUsnOrder(mTempContactGroup.getUsnOrder() + 1);

        final List<ContactsGroup> contactGroups = new ArrayList<ContactsGroup>(2);
        contactGroups.add(neighbourhood);
        contactGroups.add(mTempContactGroup);

        mDbHelper.updateContactsGroups(contactGroups);
    }

    private void updateOrdersToIndent(ContactsGroup oldParent) {
        final List<ContactsGroup> contactGroups;
        if (oldParent == null) {
            contactGroups = new ArrayList<ContactsGroup>();
            for (ITreePureNode i : mAdapter.getData()) {
                final ContactsGroup p = (ContactsGroup) i;
                if (p.getParentId() == null) {
                    contactGroups.add(p);
                }
            }
        } else {
            contactGroups = oldParent.getSubnodes();
        }

        contactGroups.remove(mTempContactGroup);

        for (int i = 0; i < contactGroups.size(); i++) {
            final ContactsGroup p = contactGroups.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        mDbHelper.updateContactsGroups(contactGroups);
    }

    @Override
    protected void onDialogPositiveButton() {
        setBlockAtUI(true);
        new Thread(mRemoveRun).start();
    }

    private Runnable mRemoveRun = new Runnable() {
        @Override
        public void run() {
            removeFeature();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private void removeFeature() {
        try {
            updateOrdersToIndent(mTempContactGroup.getParent());

            mApp.getContentResolver().insert(UidToDeleteContract.CONTENT_URI,
                    UidToDelete.getContentValues(mTempContactGroup));
            mDbHelper.getContactsGroupDao().delete(mTempContactGroup);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        mTempContactGroup = null;
    }

    private void openContactGroup(ContactsGroup contactGroup) {
        FeaturesActivity.hideActionButton();
        startFragment(PropertiesContactGroupFragment.newInstance(contactGroup));
    }

    @Override
    protected boolean onAddFeatureClick() {
        openContactGroup(null);
        return true;
    }

    @Override
    protected View getListViewHeader() {
        return null;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.contact_groups;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.groups;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return true;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        return false;
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    private int recursiveChildsCount(ContactsGroup parent) {
        int count = 0;
        if (parent == null || parent.getSubnodes() == null || parent.getSubnodes().isEmpty()) {
            return count;
        }

        for (ContactsGroup child : parent.getSubnodes()) {
            count++;
            count += recursiveChildsCount(child);
        }
        return count;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    private void setActionButtonListener(){
        FeaturesActivity.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mAdapter.getCount() >= 25 && (mSettings.getLicenseType() == mSettings.LICENSE_TYPE_FREE ||
                    mSettings.getLicenseType() == mSettings.LICENSE_TYPE_NONE)){
                    LicenseDialog.newInstance().showDialog(getActivity().getFragmentManager());
                }
                else {
                    onAddFeatureClick();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        FeaturesActivity.showActionButton();
    }
}