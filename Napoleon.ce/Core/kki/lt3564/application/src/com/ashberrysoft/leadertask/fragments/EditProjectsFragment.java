package com.ashberrysoft.leadertask.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.dialog.LicenseDialog;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.helper.TaskDeleteHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.dao.ITreePureNode;

import static com.ashberrysoft.leadertask.modern.fragment.MenuFragment.ACTION_MENU_ITEM;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EditProjectsFragment extends BaseFeaturesFragment implements OnSimpleFeatureViewListener<ITreePureNode> {

    private static final String CLASS_PATH = EditProjectsFragment.class.getName();
    private static final String EXTRA_PROJECT = CLASS_PATH + "EXTRA_PROJECT";

    // VALUE's
    private MenuInflater mMenuInflater;
    private Project mTempProject;
    private int mTempPosition;

    // ADAPTER
    private SimpleFeatureListAdapter<ITreePureNode> mAdapter;

    public static EditProjectsFragment newInstance() {
        return new EditProjectsFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mTempProject = b != null ? ((Project) b.getSerializable(EXTRA_PROJECT)) : null;
        mMenuInflater = getActivity().getMenuInflater();
        mAdapter = new SimpleFeatureListAdapter<ITreePureNode>(getActivity(), FeatureType.PROJECT, this);
        setActionButtonListener();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter.setData(SimpleFeatureListAdapter.getListProjects(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_PROJECT, mTempProject);
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    @Override
    public void onSimpleFeatureViewClick(ITreePureNode data) {
        openProject((Project) data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, ITreePureNode data, int position, ITreePureNode dataPrev, ITreePureNode dataPost) {
        mTempProject = (Project) data;
        mTempPosition = position;
        getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempProject == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        if (mTempProject.getParent() == null) {
            setMenuForRoot(menu);
        } else {
            setMenuForChild(menu);
        }
    }

    private void setMenuForRoot(ContextMenu menu) {
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        if (mTempPosition == 0) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }

        if (mTempPosition == (mAdapter.getData().size() - 1 - recursiveChildsCount(mTempProject))) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }
    }

    private void setMenuForChild(ContextMenu menu) {
        final Project parent = mTempProject.getParent();
        final List<Project> childs = parent.getSubnodes();

        int position;
        for (position = 0; position < childs.size(); position++) {
            if (mTempProject.getId().equals(childs.get(position).getId())) {
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
                openProject(mTempProject);
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
                showSimpleDialog(R.string.d_project_remove_title, R.string.d_project_remove_message);
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
        UpdateFeatureLinkHelper.updateProjectTotalLink(mApp);

        mAdapter.setData(SimpleFeatureListAdapter.getListProjects(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    private void goLeft() {
        try {
            final Project parent = mTempProject.getParent();
            final Project parentParent = parent.getParent();

            final List<Project> projects;
            if (parentParent == null) {
                projects = new ArrayList<Project>();
                for (ITreePureNode i : mAdapter.getData()) {
                    final Project p = (Project) i;
                    if (p.getParentId() == null) {
                        projects.add(p);
                    }
                }
            } else {
                projects = parentParent.getSubnodes();
            }

            int parentPosition = -1;
            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getId().equals(parent.getId())) {
                    parentPosition = i;
                    break;
                }
            }

            mTempProject.setParentId(parent.getParentId());
            mTempProject.setUsnParent(mTempProject.getUsnParent() + 1);
            projects.add(parentPosition + 1, mTempProject);

            for (int i = 0; i < projects.size(); i++) {
                final Project p = projects.get(i);
                p.setUsnPlusPlus();
                p.setOrder(i + 1);
                p.setUsnOrder(p.getUsnOrder() + 1);
            }

            mDbHelper.updateProjects(projects);
        } catch (Exception e) {

        }
    }

    private void goRight() {
        try {
            int newParentPosition = mTempPosition - 1;
            Project newParent = null;
            while ((newParent = (Project) mAdapter.getData().get(newParentPosition)).getIndent() != mTempProject
                    .getIndent()) {
                newParentPosition--;
            }

            final Project oldParent = mTempProject.getParent();
            mTempProject.setParentId(newParent.getId());
            mTempProject.setUsnParent(mTempProject.getUsnParent() + 1);

            final List<Project> childs = newParent.getSubnodes();
            childs.add(mTempProject);

            for (int i = 0; i < childs.size(); i++) {
                final Project p = childs.get(i);
                p.setUsnPlusPlus();
                p.setOrder(i + 1);
                p.setUsnOrder(p.getUsnOrder() + 1);
            }

            updateOrdersToIndent(oldParent);
            mDbHelper.updateProjects(childs);
        } catch (Exception e) {

        }
    }

    private void goUp() {
        try {
            int newParentPosition = mTempPosition - 1;
            Project neighbourhood = null;
            while ((neighbourhood = (Project) mAdapter.getData().get(newParentPosition)).getIndent() != mTempProject.getIndent()) {
                newParentPosition--;
            }

            final int neighbourhoodOrder = neighbourhood.getOrder();
            neighbourhood.setOrder(mTempProject.getOrder());
            neighbourhood.setUsnPlusPlus();
            neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

            mTempProject.setOrder(neighbourhoodOrder);
            mTempProject.setUsnPlusPlus();
            mTempProject.setUsnOrder(mTempProject.getUsnOrder() + 1);

            final List<Project> projects = new ArrayList<Project>(2);
            projects.add(neighbourhood);
            projects.add(mTempProject);

            mDbHelper.updateProjects(projects);
        } catch (Exception e) {

        }
    }

    private void goDown() {
        try {
            int newParentPosition = mTempPosition + 1;
            Project neighbourhood = null;
            while ((neighbourhood = (Project) mAdapter.getData().get(newParentPosition)).getIndent() != mTempProject
                    .getIndent()) {
                newParentPosition++;
            }

            final int neighbourhoodOrder = neighbourhood.getOrder();
            neighbourhood.setOrder(mTempProject.getOrder());
            neighbourhood.setUsnPlusPlus();
            neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

            mTempProject.setOrder(neighbourhoodOrder);
            mTempProject.setUsnPlusPlus();
            mTempProject.setUsnOrder(mTempProject.getUsnOrder() + 1);

            final List<Project> projects = new ArrayList<Project>(2);
            projects.add(neighbourhood);
            projects.add(mTempProject);

            mDbHelper.updateProjects(projects);
        } catch (Exception e) {

        }
    }

    private void updateOrdersToIndent(Project oldParent) {
        final List<Project> projects;
        if (oldParent == null) {
            projects = new ArrayList<Project>();
            for (ITreePureNode i : mAdapter.getData()) {
                final Project p = (Project) i;
                if (p.getParentId() == null) {
                    projects.add(p);
                }
            }
        } else {
            projects = oldParent.getSubnodes();
        }

        projects.remove(mTempProject);

        for (int i = 0; i < projects.size(); i++) {
            final Project p = projects.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        mDbHelper.updateProjects(projects);
    }

    @Override
    protected void onDialogPositiveButton() {
        setBlockAtUI(true);
        new Thread(mRemoveRun).start();
    }

    private Runnable mRemoveRun = new Runnable() {
        @Override
        public void run() {
                delMyTasks();
                removeFeature();
                updateAdapterData();
                setBlockAtUI(false);
        }
    };

    private void delMyTasks() {
        // выдрать все свои задачи из проекта и удалить их
        StringBuilder mSb = new StringBuilder();
        ArrayList <LTask> myTasksFromProject = new ArrayList<>();
        Cursor cursorTasks = null;
        try {
            Utils.clearStringBuilder(mSb);
            cursorTasks =  getActivity().getContentResolver().query(LionMetaData.LTaskContract.CONTENT_URI, null, LeaderTaskProviderMetaData.SelectionKeeper.equals(mSb, LionMetaData.LTaskContract.UidProject, mTempProject.getId().toString().toUpperCase()), null, null);
            if (cursorTasks.getCount() > 0) {
                while (cursorTasks.moveToNext()) {
                    myTasksFromProject.add(new LTask(cursorTasks));
                }
            }
        } finally {
            if (cursorTasks != null) {
                cursorTasks.close();
            }
        }
        //
        for (LTask task: myTasksFromProject) {
            if (task.getEmailCustomer().equals(mSettings.getUserName())) {
                new TaskDeleteHelper(mApp, task, true).start();
            }
        }
    }

    private void removeFeature() {
        try {
            updateOrdersToIndent(mTempProject.getParent());

            mApp.getContentResolver().insert(UidToDeleteContract.CONTENT_URI,
                    UidToDelete.getContentValues(mTempProject));
            mDbHelper.getProjectDao().delete(mTempProject);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(mApp, mTempProject);

        mTempProject = null;
    }

    private void openProject(Project project) {
        FeaturesActivity.hideActionButton();
        startFragment(PropertiesProjectFragment.newInstance(project));
    }

    @Override
    protected boolean onAddFeatureClick() {
        openProject(null);
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
        return R.string.task_project;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.project;
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

    private int recursiveChildsCount(Project parent) {
        int count = 0;
        if (parent == null || parent.getSubnodes() == null || parent.getSubnodes().isEmpty()) {
            return count;
        }

        for (Project child : parent.getSubnodes()) {
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