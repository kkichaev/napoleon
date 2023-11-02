package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.ProjectsTreeRoot;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.views.ProjectListItemView;
import com.ashberrysoft.leadertask.views.ProjectListItemView.OnProjectItemViewListener;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.adapters.TreeAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Адаптер дерева проектов для диалога выбора проекта
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class ProjectsTreeAdapter extends TreeAdapter implements OnProjectItemViewListener {

    // VALUE's
    private Project mCurrentProject;
    private DbHelper mDbHelper;

    public ProjectsTreeAdapter(Context context, Project currentProject) {
        super(context, new ProjectsTreeRoot(context),
                new CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>>() {
                    @Override
                    public IDataView<ITreePureNode> createView(Context context, int type) {
                        return new ProjectListItemView(context);
                    }
                });
        mCurrentProject = currentProject;
        mDbHelper = DbHelper.getInstance(mContext);
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final ProjectListItemView v = cV != null ? (ProjectListItemView) cV : new ProjectListItemView(mContext, this);

        v.setData((ITreePureNode) getItem(position));
        v.setChecked(mCurrentProject);

        return v;
    }

    public Project getSelectedProject() {
        return mCurrentProject;
    }

    @Override
    public void onProjectClick(Project project, boolean isChecked) {
        if (isChecked) {
            mCurrentProject = project;
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onProjectOpen(Project project, boolean isCollapsed) {
        project.setCollapsed(!isCollapsed);
        project.setUsnCollapsed(project.getUsnCollapsed() + 1);
        project.setUsn(0);
        // TODO: update ProjectLink

        mDbHelper.updateProject(project);

        this.notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return ETreeDataNodeLevel.PROJECT.ordinal() + 1;
    }
}