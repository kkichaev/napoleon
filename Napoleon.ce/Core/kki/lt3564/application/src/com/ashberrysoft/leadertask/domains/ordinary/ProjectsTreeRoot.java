package com.ashberrysoft.leadertask.domains.ordinary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Отображение фильтров
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class ProjectsTreeRoot implements ITreeData<Project> {
    private SlidingMenuHeader mHeaderProj;

    private List<Project> mItems;

    public ProjectsTreeRoot(Context context) {
        mItems = new ArrayList<Project>();
        updateProjects(context);
    }

    public void updateProjects(Context context) {

        if (mHeaderProj != null) {
            mItems.remove(mHeaderProj);
        }
        DbHelper dbh = DbHelper.getInstance(context);
        try {
            // add own projects
            List<Project> listProjects = dbh.getProjects(context, false);

            // add projects that available for me
            listProjects.addAll(dbh.getAvailableProject(context, false));

            // sort all projects by order
            // int value = 0;
            // if (s1.getOrder() > s2.getOrder())
            // value = 1;
            // else if (s1.getOrder() < s2.getOrder())
            // value = -1;
            // else if (s1.getOrder() == s2.getOrder())
            // value = 0;
            // return value;

            /*Collections.sort(listProjects, new Comparator<Project>() {
                public int compare(Project s1, Project s2) {
                    if (s1.getOrder() > s2.getOrder()) {
                        return 1;
                    }

                    else if (s1.getOrder() < s2.getOrder()) {
                        return -1;
                    }

                    return 0;
                }
            });*/

            for (Project project : listProjects) {
                if (project.getParentId() == null) {
                    project.setExpanded(project.isCollapsed());
                    mItems.add(project);
                    processProjects(project, listProjects);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Create tree hierarchy for projects/available projects
     * 
     * @param project
     *            - particular project/available project
     * @param projects
     *            - all projects/available projects
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    private void processProjects(Project project, List<Project> projects) {
        for (Project proj : projects)
            if (project.getId().equals(proj.getParentId())) {
                project.addChild(proj);
                processProjects(proj, projects);
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
    public List<Project> getSubnodes() {
        return mItems;
    }

    @Override
    public void setExpanded(boolean value) {
    }
}
