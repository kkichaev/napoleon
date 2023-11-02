package com.ashberrysoft.leadertask.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ContactsGroupPropertiesAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.dialogs.SetMultiPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.PerformerListItemViewContactsGroup.OnPerformerListItemListener;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView.OnFeaturePropertiesHeaderListener;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesContactGroupFragment extends BaseFeaturesFragment implements OnFeaturePropertiesHeaderListener, OnPerformerListItemListener{

    private static final String CLASS_PATH = PropertiesContactGroupFragment.class.getSimpleName();
    private static final String EXTRA_CONTACT_GROUP = CLASS_PATH + "EXTRA_CONTACT_GROUP";
    private static final String EXTRA_PERFORMERS = CLASS_PATH + "EXTRA_PERFORMERS";
    private static final String EXTRA_CONTACT_GROUP_NEW = CLASS_PATH + "EXTRA_CONTACT_GROUP_NEW";
    private static final String EXTRA_PERFORMER_POSITION = CLASS_PATH + "EXTRA_PERFORMER_POSITION";
    private static final String SPLIT_SYMBOL = "\\.\\.";
    private static final String DOUBLE_DOTS = "..";

    // VIEW's
    private PropertiesFeatureHeaderView mHeaderView;

    // VALUE's
    private ContactsGroup mContactsGroup;
    private boolean mContactsGroupNew;
    private int mPosition;
    private boolean mShowKeyBoard;

    // ADAPTER
    private ContactsGroupPropertiesAdapter mAdapter;

    public static PropertiesContactGroupFragment newInstance(ContactsGroup contactGroup) {
        final PropertiesContactGroupFragment f = new PropertiesContactGroupFragment();

        if (contactGroup != null) {
            final Bundle b = new Bundle();
            b.putSerializable(EXTRA_CONTACT_GROUP, contactGroup);
            f.setArguments(b);
        }

        return f;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        List<String> performers = null;
        final Bundle bundle = b != null ? b : getArguments();
        if (bundle != null) {
            mPosition = bundle.getInt(EXTRA_PERFORMER_POSITION);
            mContactsGroupNew = bundle.getBoolean(EXTRA_CONTACT_GROUP_NEW, false);
            mContactsGroup = (ContactsGroup) bundle.get(EXTRA_CONTACT_GROUP);
            performers = (List<String>) bundle.getSerializable(EXTRA_PERFORMERS);
            mShowKeyBoard = false;

        } else {
            mContactsGroupNew = true;
            mContactsGroup = new ContactsGroup();
            mShowKeyBoard = true;
        }

        mHeaderView = new PropertiesFeatureHeaderView(getActivity(), this);
        mHeaderView.setContactsGroupData(mContactsGroup);

        mAdapter = new ContactsGroupPropertiesAdapter(getActivity(), this);
        if(!mContactsGroupNew) {
            if (performers == null && mContactsGroup.getSharedUsers() != null) {
                final String[] users = mContactsGroup.getSharedUsers().split(SPLIT_SYMBOL);
                performers = new ArrayList<String>(users.length);
                for (String u : users) {
                    performers.add(u);
                }
            } else if (performers == null) {
                performers = new ArrayList<String>(0);
            }
            mAdapter.setData(performers, mContactsGroup);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mShowKeyBoard) {
            showKeyboard(mHeaderView.getEditText());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        mListView = (ListView) inflater.inflate(R.layout.edit_features_fragment, container, false);
        if (getListViewHeader() != null) {
            mListView.addHeaderView(getListViewHeader());
        }
        final List<Fragment> AllFragments = getFragmentManager().getFragments();
        if(!mContactsGroupNew) {
            if (mContactsGroup.getCreator().equals(LTSettings.getInstance().getUserName())) {
                final View footer = View.inflate(mApp, R.layout.custom_footer_to_contacts_group_adapter, null);
                footer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OnFooterClick();
                    }
                });
                mListView.addFooterView(footer, null, false);
            }
        }
        else {
            final View footer = View.inflate(mApp, R.layout.custom_footer_to_contacts_group_adapter, null);
            footer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OnFooterClick();
                }
            });
            mListView.addFooterView(footer, null, false);
        }
        mListView.setAdapter(getAdapter());
        registerForContextMenu(mListView);

        return mListView;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            mContactsGroup.setName(mHeaderView.getName());
        }
        if (!TextUtils.isEmpty(mHeaderView.getComment().trim())) {
            mContactsGroup.setComment(mHeaderView.getComment());
        }
        b.putSerializable(EXTRA_CONTACT_GROUP, mContactsGroup);
        b.putSerializable(EXTRA_PERFORMERS, (Serializable) mAdapter.getData());
        b.putBoolean(EXTRA_CONTACT_GROUP_NEW, mContactsGroupNew);
        b.putInt(EXTRA_PERFORMER_POSITION, mPosition);
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) { return false; }

    private void OnFooterClick()
    {
        SetMultiPerformerDialog.newInstanceCustomTitle(this, R.string.contacts_group_add_performer).showDialog(getFragmentManager());
    }
    @Override
    public void onFragmentResult(Object object, int requestCode) {
        switch (requestCode) {
            case SetMultiPerformerDialog.REQUEST_CODE:
                final ArrayList<String> performers = (ArrayList<String>) object;
                for (String performer : performers) {
                    if (isPerformerUnique(performer)) {
                        mAdapter.getData().add(performer);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Utils.showToast(getActivity(), R.string.t_error_performer_not_unique);
                    }
                }
                break;

            default:
                super.onFragmentResult(object, requestCode);
        }
    }

    private boolean isPerformerUnique(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        }

        for (String p : mAdapter.getData()) {
            if (p.equalsIgnoreCase(s)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onFeaturePropertiesChecked(int id, boolean isChecked) {
        switch (id) {
            default:
                break;
        }
    }

    @Override
    public void onPerformerRemove(int position) {
        mPosition = position;
        showSimpleDialog(R.string.d_performer_remove_title, R.string.d_performer_remove_message);
    }

    @Override
    protected void onDialogPositiveButton() {
        mAdapter.getData().remove(mPosition);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected View getListViewHeader() {
        return mHeaderView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        if (mContactsGroup == null || TextUtils.isEmpty(mContactsGroup.getName())) {
            return R.string.contacts_group_new;
        } else {
            return R.string.contacts_group_properties;
        }
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.groups;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        inputHide(mHeaderView);
        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            new Thread(mSaveContactsGroupRunnable).start();
        } else {
            Utils.showToast(getActivity(), R.string.t_error_feature_name);
        }

        return true;
    }

    private final Runnable mSaveContactsGroupRunnable = new Runnable() {
        @Override
        public void run() {
            mContactsGroup.setName(mHeaderView.getName());
            mContactsGroup.setComment(mHeaderView.getComment());

            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                sb.append(mAdapter.getData().get(i));
                if (i < mAdapter.getData().size() - 1) {
                    sb.append(DOUBLE_DOTS);
                }
            }
            final String performers = sb.toString();
            if (TextUtils.isEmpty(performers)) {
                mContactsGroup.setSharedUsers(null);
            } else {
                mContactsGroup.setSharedUsers(performers);
            }

            mContactsGroup.setUsn(0);
            mContactsGroup.setUsnName(mContactsGroup.getUsnName() + 1);
            mContactsGroup.setUsnSharedUsers(mContactsGroup.getUsnSharedUsers() + 1);
            mContactsGroup.setUsnComment(mContactsGroup.getUsnComment() + 1);

            if (mContactsGroupNew) {
                mContactsGroup.setId(UUID.randomUUID());
                mContactsGroup.setCreator(mSettings.getUserName());

                mContactsGroup.setOrder(mSettings.getLastFeatureOrder() + 1);
                mContactsGroup.setUsnOrder(mContactsGroup.getUsnOrder() + 1);

                mContactsGroup.setParent(null);
                mContactsGroup.setParentId(null);
                mContactsGroup.setUsnParent(mContactsGroup.getUsnParent() + 1);
            }

            try {
                if (mContactsGroupNew) {
                    mDbHelper.getContactsGroupDao().create(mContactsGroup);

                } else {
                    mDbHelper.getContactsGroupDao().update(mContactsGroup);
                }

            } catch (SQLException e) {
                Utils.toLog(e);
            }
        }
    };

    @Override
    protected boolean onAddFeatureClick() {
        return false;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    @Override
    public void onDetach() { super.onDetach();}

}