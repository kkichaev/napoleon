package com.ashberrysoft.leadertask.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.v2soft.AndLib.dao.ITreePureNode;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EditContactsFragment extends BaseFeaturesFragment implements OnSimpleFeatureViewListener<ITreePureNode>{

    private static final String CLASS_PATH = EditContactsFragment.class.getName();
    private static final String EXTRA_CONTACT = CLASS_PATH + "EXTRA_CONTACT";
    private static final String KEY_SAVED_QUERY = CLASS_PATH + "KEY_SAVED_QUERY";

    // VALUE's
    private MenuInflater mMenuInflater;
    private Contact mTempContact;
    private int mTempPosition;
    private SearchView mSearchView;
    private String mCurrentQuery;

    // ADAPTER
    private SimpleFeatureListAdapter<ITreePureNode> mAdapter;

    public static EditContactsFragment newInstance() {
        return new EditContactsFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mTempContact = b != null ? ((Contact) b.getSerializable(EXTRA_CONTACT)) : null;
        mCurrentQuery = b != null ? ((String) b.getString(KEY_SAVED_QUERY)) : null;
        mTempContact = b != null ? ((Contact) b.getSerializable(EXTRA_CONTACT)) : null;
        mMenuInflater = getActivity().getMenuInflater();
        mAdapter = new SimpleFeatureListAdapter<ITreePureNode>(getActivity(), FeatureType.CONTACTS, this);
        setActionButtonListener();
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setData(SimpleFeatureListAdapter.getListContacts(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_CONTACT, mTempContact);
        if (mSearchView != null && mSearchView.getQuery() != null) {
            b.putString(KEY_SAVED_QUERY, mSearchView.getQuery().toString());
        }
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    @Override
    public void onSimpleFeatureViewClick(ITreePureNode data) {
        openContactContact((Contact) data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, ITreePureNode data, int position, ITreePureNode dataPrev, ITreePureNode dataPost) {
        mTempContact = (Contact) data;
        mTempPosition = position;
        getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setActionBarTitle(getString(getActionBarTitle()) + addTriangleToText());
        inflater.inflate(R.menu.contacts_fragment, menu);
        MenuItem searchItem = menu.findItem(R.id.search);

        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setQueryHint(getString(R.string.search));
        if (!TextUtils.isEmpty(mCurrentQuery)) {
            mSearchView.setQuery(mCurrentQuery, true);
            mSearchView.setFocusable(true);
            mSearchView.setIconified(false);
            mSearchView.requestFocusFromTouch();
        }

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateAdapterData();
                return false;
            }
        });

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //нажали поиск
                if (query.length() > 0) {
                    updateAdapterDataAfterSearch(query);
                    return true;
                } else {
                    updateAdapterData();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //посимвольно
                if (newText.length() > 0) {
                    updateAdapterDataAfterSearch(newText);
                    return true;
                } else {
                    updateAdapterData();
                    return false;
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_name_contacts:
                setContactsOrder(1);
                return true;

            case R.id.order_default_contacts:
                setContactsOrder(0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setContactsOrder(int order) {
        mSettings.setContactsOrder(order);
        setActionBarTitle(getString(getActionBarTitle()) + addTriangleToText());
        updateAdapterData();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempContact == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        if (mTempContact.getUidParent() == null) {
            setMenuForRoot(menu);
        } else {
            setMenuForChild(menu);
        }

        setMenuForCreator(menu);
    }

    private void setMenuForRoot(ContextMenu menu) {
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        if (mSettings.getContactsOrder() == 0) {
            if (mTempPosition == 0) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
                setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
            }

            if (mTempPosition == (mAdapter.getData().size() - 1 - recursiveChildsCount(mTempContact))) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
            }
        } else {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        }
    }

    private void setMenuForCreator(ContextMenu menu) {
        if (!mTempContact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
            setMenuItemEnabled(menu.findItem(R.id.menu_dell), false);
        }
    }

    private void setMenuForChild(ContextMenu menu) {
        final Contact parent = mTempContact.getParent();
        final List<Contact> childs = parent.getSubnodes();

        int position;
        for (position = 0; position < childs.size(); position++) {
            if (mTempContact.getId().equals(childs.get(position).getId())) {
                break;
            }
        }
        if (mSettings.getContactsOrder() == 0) {
            if (position == 0) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
                setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
            }

            if (position == childs.size() - 1) {
                setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
            }
        } else {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_properties:
                openContactContact(mTempContact);
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
                showSimpleDialog(R.string.d_contact_remove_title, R.string.d_contact_remove_message);
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
        mAdapter.setData(SimpleFeatureListAdapter.getListContacts(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    private void updateAdapterDataAfterSearch(String search) {
        mAdapter.setData(SimpleFeatureListAdapter.getListContactsWithSearch(mSettings, mDbHelper, search));
        adapterNotifyDataSetChanged();
    }

    private void goLeft() {
        final Contact parent = mTempContact.getParent();
        final Contact parentParent = parent.getParent();

        final List<Contact> contactContacts;
        if (parentParent == null) {
            contactContacts = new ArrayList<Contact>();
            for (ITreePureNode i : mAdapter.getData()) {
                final Contact p = (Contact) i;
                if (p.getUidParent() == null) {
                    contactContacts.add(p);
                }
            }
        } else {
            contactContacts = parentParent.getSubnodes();
        }

        int parentPosition = -1;
        for (int i = 0; i < contactContacts.size(); i++) {
            if (contactContacts.get(i).getId().equals(parent.getId())) {
                parentPosition = i;
                break;
            }
        }

        mTempContact.setUidParent(parent.getUidParent());
        mTempContact.setUsnFieldUidParent(mTempContact.getUsnFieldUidParent() + 1);
        contactContacts.add(parentPosition + 1, mTempContact);

        for (int i = 0; i < contactContacts.size(); i++) {
            final Contact p = contactContacts.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnFieldOrder(p.getUsnFieldOrder() + 1);
        }

        mDbHelper.updateContacts(contactContacts);
    }

    private void goRight() {
        int newParentPosition = mTempPosition - 1;
        Contact newParent = null;
        while ((newParent = (Contact) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContact
                .getIndent()) {
            newParentPosition--;
        }

        final Contact oldParent = mTempContact.getParent();
        mTempContact.setUidParent(newParent.getId());
        mTempContact.setUsnFieldUidParent(mTempContact.getUsnFieldUidParent() + 1);

        final List<Contact> childs = newParent.getSubnodes();
        childs.add(mTempContact);

        for (int i = 0; i < childs.size(); i++) {
            final Contact p = childs.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnFieldOrder(p.getUsnFieldOrder() + 1);
        }

        updateOrdersToIndent(oldParent);
        mDbHelper.updateContacts(childs);
    }

    private void goUp() {
        int newParentPosition = mTempPosition - 1;
        Contact neighbourhood = null;
        while ((neighbourhood = (Contact) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContact
                .getIndent()) {
            newParentPosition--;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempContact.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnFieldOrder(neighbourhood.getUsnFieldOrder() + 1);

        mTempContact.setOrder(neighbourhoodOrder);
        mTempContact.setUsnPlusPlus();
        mTempContact.setUsnFieldOrder(mTempContact.getUsnFieldOrder() + 1);

        final List<Contact> contactContacts = new ArrayList<Contact>(2);
        contactContacts.add(neighbourhood);
        contactContacts.add(mTempContact);

        mDbHelper.updateContacts(contactContacts);
    }

    private void goDown() {
        int newParentPosition = mTempPosition + 1;
        Contact neighbourhood = null;
        while ((neighbourhood = (Contact) mAdapter.getData().get(newParentPosition)).getIndent() != mTempContact
                .getIndent()) {
            newParentPosition++;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempContact.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnFieldOrder(neighbourhood.getUsnFieldOrder() + 1);

        mTempContact.setOrder(neighbourhoodOrder);
        mTempContact.setUsnPlusPlus();
        mTempContact.setUsnFieldOrder(mTempContact.getUsnFieldOrder() + 1);

        final List<Contact> contactContacts = new ArrayList<Contact>(2);
        contactContacts.add(neighbourhood);
        contactContacts.add(mTempContact);

        mDbHelper.updateContacts(contactContacts);
    }


    
    private void updateOrdersToIndent(Contact oldParent) {
        final List<Contact> contactContacts;
        if (oldParent == null) {
            contactContacts = new ArrayList<Contact>();
            for (ITreePureNode i : mAdapter.getData()) {
                final Contact p = (Contact) i;
                if (p.getUidParent() == null) {
                    contactContacts.add(p);
                }
            }
        } else {
            contactContacts = oldParent.getSubnodes();
        }

        contactContacts.remove(mTempContact);

        for (int i = 0; i < contactContacts.size(); i++) {
            final Contact p = contactContacts.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnFieldOrder(p.getUsnFieldOrder() + 1);
        }

        mDbHelper.updateContacts(contactContacts);
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
            updateOrdersToIndent(mTempContact.getParent());

            mApp.getContentResolver().insert(UidToDeleteContract.CONTENT_URI,
                    UidToDelete.getContentValues(mTempContact));
            mDbHelper.getContactDao().delete(mTempContact);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        mTempContact = null;
    }

    private void openContactContact(Contact contact) {
        FeaturesActivity.hideActionButton();
        startFragment(PropertiesContactFragment.newInstance(contact));
    }

    @Override
    public void onResume() {
        super.onResume();

        FeaturesActivity.showActionButton();
    }

    @Override
    protected boolean onAddFeatureClick() {
        openContactContact(null);
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
        return R.string.contacts;
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

    private int recursiveChildsCount(Contact parent) {
        int count = 0;
        if (parent == null || parent.getSubnodes() == null || parent.getSubnodes().isEmpty()) {
            return count;
        }

        for (Contact child : parent.getSubnodes()) {
            count++;
            count += recursiveChildsCount(child);
        }
        return count;
    }

    public boolean isSearchShowing() {
        return mSearchView.isIconified() ? false : true;
    }

    public void closeSearchView() {
        mSearchView.setIconified(true);
        mSearchView.setIconified(true);
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

    private Spanned addTriangleToText() {
        if(mSettings.getContactsOrder() != 0) {
            return Html.fromHtml(SharedStrings.SPACE + SharedStrings.ARROW);
        }
        else {
            return Html.fromHtml("");
        }
    }
}