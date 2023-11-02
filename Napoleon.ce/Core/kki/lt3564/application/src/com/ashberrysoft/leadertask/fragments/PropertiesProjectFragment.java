package com.ashberrysoft.leadertask.fragments;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.ProjectPropertiesAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.dialogs.SetMultiPerformerDialog;
import com.ashberrysoft.leadertask.dialogs.SetPerformerDialog;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.PerformerListItemView.OnPerformerListItemListener;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView;
import com.ashberrysoft.leadertask.views.PropertiesFeatureHeaderView.OnFeaturePropertiesHeaderListener;

import static com.ashberrysoft.leadertask.R.id.view;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PropertiesProjectFragment extends BaseFeaturesFragment implements OnFeaturePropertiesHeaderListener, OnPerformerListItemListener{

    private static final String CLASS_PATH = PropertiesProjectFragment.class.getSimpleName();
    private static final String EXTRA_PROJECT = CLASS_PATH + "EXTRA_PROJECT";
    private static final String EXTRA_PERFORMERS = CLASS_PATH + "EXTRA_PERFORMERS";
    private static final String EXTRA_PROJECT_NEW = CLASS_PATH + "EXTRA_PROJECT_NEW";
    private static final String EXTRA_PERFORMER_POSITION = CLASS_PATH + "EXTRA_PERFORMER_POSITION";
    private static final String SPLIT_SYMBOL = "\\.\\.";
    private static final String DOUBLE_DOTS = "..";

    // VIEW's
    private PropertiesFeatureHeaderView mHeaderView;

    // VALUE's
    private Project mProject;
    private boolean mProjectNew;
    private int mPosition;
    private boolean mShowKeyBoard;

    // ADAPTER
    private ProjectPropertiesAdapter mAdapter;

    public static PropertiesProjectFragment newInstance(Project project) {
        final PropertiesProjectFragment f = new PropertiesProjectFragment();

        if (project != null) {
            final Bundle b = new Bundle();
            b.putSerializable(EXTRA_PROJECT, project);
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
            mProjectNew = bundle.getBoolean(EXTRA_PROJECT_NEW, false);
            mProject = (Project) bundle.get(EXTRA_PROJECT);
            performers = (List<String>) bundle.getSerializable(EXTRA_PERFORMERS);
            mShowKeyBoard = false;

        } else {
            mProjectNew = true;
            mProject = new Project();
            mShowKeyBoard = true;
        }

        mHeaderView = new PropertiesFeatureHeaderView(getActivity(), this);
        mHeaderView.setProjectData(mProject);
        if (mProject.getCreator() != null) {
            if (!mProject.getCreator().equals(LTSettings.getInstance().getUserName())) {
                mHeaderView.getEditText().setEnabled(false);
            } else {
                mHeaderView.getEditText().setEnabled(true);
            }
        }

        mAdapter = new ProjectPropertiesAdapter(getActivity(), this);

        if (performers == null && mProject.getSharedUsers() != null) {
            final String[] users = mProject.getSharedUsers().split(SPLIT_SYMBOL);
            performers = new ArrayList<String>(users.length);
            for (String u : users) {
                performers.add(u);
            }
        } else if (performers == null) {
            performers = new ArrayList<String>(0);
        }

        mAdapter.setData(performers);
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
        final View footer;
        if (mSettings.isThemeDark()) {
            footer = View.inflate(mApp, R.layout.custom_footer_to_project_user_adapter_black, null);
        } else {
            footer = View.inflate(mApp, R.layout.custom_footer_to_project_user_adapter, null);
        }
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnFooterClick();
            }
        });
        if (mProject.getCreator() != null) {
            if (mProject.getCreator().equals(LTSettings.getInstance().getUserName())) {
                mListView.addFooterView(footer, null, false);
            }
        } else {
            if (mProject != null) {
                mListView.addFooterView(footer, null, false);
            }
        }

        mListView.setAdapter(getAdapter());
        registerForContextMenu(mListView);

        return mListView;
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            mProject.setName(mHeaderView.getName());
        }
        if (!TextUtils.isEmpty(mHeaderView.getComment().trim())) {
            mProject.setComment(mHeaderView.getComment());
        }
        b.putSerializable(EXTRA_PROJECT, mProject);
        b.putSerializable(EXTRA_PERFORMERS, (Serializable) mAdapter.getData());
        b.putBoolean(EXTRA_PROJECT_NEW, mProjectNew);
        b.putInt(EXTRA_PERFORMER_POSITION, mPosition);
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) { return false; }

    private void OnFooterClick()
        {
        SetMultiPerformerDialog.newInstanceCustomTitle(this, R.string.project_add_performer).showDialog(getFragmentManager());
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
            case R.id.cbClose:
                mProject.setQuiet(isChecked);
                mProject.setUsnQuiet(mProject.getUsnQuiet() + 1);
                break;
            case R.id.cbProjectTasks:
                mProject.setGroup(isChecked);
                mProject.setUsnGroup(mProject.getUsnGroup() + 1);
                break;

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
        if (mProject == null || TextUtils.isEmpty(mProject.getName())) {
            return R.string.project_new;
        } else {
            return R.string.project_properties;
        }
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.project;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return false;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        inputHide(mHeaderView);
        if (!TextUtils.isEmpty(mHeaderView.getName().trim())) {
            new Thread(mSaveProjectRunnable).start();
        } else {
            Utils.showToast(getActivity(), R.string.t_error_feature_name);
        }

        return true;
    }

    private final Runnable mSaveProjectRunnable = new Runnable() {
        @Override
        public void run() {
            mProject.setName(mHeaderView.getName());
            mProject.setComment(mHeaderView.getComment());

            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mAdapter.getData().size(); i++) {
                sb.append(mAdapter.getData().get(i));
                if (i < mAdapter.getData().size() - 1) {
                    sb.append(DOUBLE_DOTS);
                }
            }
            final String performers = sb.toString();
            if (TextUtils.isEmpty(performers)) {
                mProject.setSharedUsers(null);
            } else {
                mProject.setSharedUsers(performers);
            }

            mProject.setUsn(0);
            mProject.setUsnName(mProject.getUsnName() + 1);
            mProject.setUsnSharedUsers(mProject.getUsnSharedUsers() + 1);
            mProject.setUsnComment(mProject.getUsnComment() + 1);

            if (mProjectNew) {
                mProject.setId(UUID.randomUUID());
                mProject.setCreator(mSettings.getUserName());

                mProject.setOrder(mSettings.getLastFeatureOrder() + 1);
                mProject.setUsnOrder(mProject.getUsnOrder() + 1);

                mProject.setParent(null);
                mProject.setParentId(null);
                mProject.setUsnParent(mProject.getUsnParent() + 1);
            }

            try {
                if (mProjectNew) {
                    mDbHelper.getProjectDao().create(mProject);

                } else {
                    mDbHelper.getProjectDao().update(mProject);
                }

            } catch (SQLException e) {
                Utils.toLog(e);
            }

            final UpdateFeatureLinkHelper linkHelper = new UpdateFeatureLinkHelper(mApp);
            if (mProjectNew) {
                linkHelper.createTotalLink(mProject);

            } else {
                linkHelper.updateProjectTotalLink(mProject);
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