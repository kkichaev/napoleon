package com.ashberrysoft.leadertask.domains.ordinary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.GetNumberOfIncomeTasks;
import com.ashberrysoft.leadertask.data_providers.GetNumberOfTasksForToday;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Отображение фильтров
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
public class SlidingMenuRootTreeItem implements ITreeData<ITreeData<?>> {

    private static final UUID sProjectsRootUUID = UUID.fromString("9267ecdc-7ec8-45b5-a33b-b9962fd37d7f");
    private static final UUID sInstructIRootUUID = UUID.fromString("5161259e-e433-41c1-8bf6-a64ed386bd02");
    private static final UUID sInstructMeRootUUID = UUID.fromString("61249fe6-a229-4284-96b0-b88dae2c0d9a");
    private static final UUID sAvaiableMeRootUUID = UUID.fromString("b63122b1-2bff-425e-908f-49a92d397008");

    // VALUE's
    private SlidingMenuHeader mHeaderProj;
    private SlidingMenuHeader mHeaderAvalaibleProj;
    private SlidingMenuHeader mHeaderCat;
    private SlidingMenuHeader mHeaderInstructI;
    private SlidingMenuHeader mHeaderInstructMe;
    private Context mContext;
    private List<ITreeData<?>> mItems;
    private DbHelper mDbHelper;

    private SlidingMenuHeader mToday = new SlidingMenuHeader(FilterNumberTask.RECORD_TODAY,
            FilterNumberTask.sTodayRecordUUID) {
        @Override
        public int getNodeLevel() {
            return ETreeDataNodeLevel.TODAY.ordinal();
        }

        @Override
        public boolean isExpandable() {
            return false;
        }

        @Override
        public int getIndent() {
            return 0;
        }
    };

    private SlidingMenuHeader mInbox = new SlidingMenuHeader(FilterNumberTask.RECORD_INCOME,
            FilterNumberTask.sIncomeRecordUUID) {
        @Override
        public int getNodeLevel() {
            return ETreeDataNodeLevel.INBOX.ordinal();
        }

        @Override
        public boolean isExpandable() {
            return false;
        }

        @Override
        public int getIndent() {
            return 0;
        }
    };

    public SlidingMenuRootTreeItem(Context context, String userName) {
        if (context == null) {
            throw new NullPointerException();
        }

        mContext = context.getApplicationContext();
        mDbHelper = DbHelper.getInstance(context);

        mItems = new ArrayList<ITreeData<?>>(2);
        mItems.add(mToday);
        mItems.add(mInbox);

        try {
            new GetNumberOfIncomeTasks(mContext, userName).execute(null);
            // TODO Bug #3460
            new GetNumberOfTasksForToday(mContext, userName).execute(null);
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }

        updateInstructI(mContext, userName, false, null);
        updateAssignedToMe(mContext, userName, false, null);
        updateProjects(mContext, false);
        updateAvailableProjects(mContext, false);
        updateCategories(mContext, false, userName);

        expandedHeader();
    }

    /**
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     */
    public void expandedHeader() {
        // final LTSettings settings = ((LeaderTaskApplication) mContext.getApplicationContext()).getSettings();

        if (mHeaderInstructI != null) {
            // mHeaderInstructI.setExpanded(settings.isSlidingInstructIExpande());
            mHeaderInstructI.setExpanded(true);
        }

        if (mHeaderInstructMe != null) {
            // mHeaderInstructMe.setExpanded(settings.isSlidingInstructMyExpande());
            mHeaderInstructMe.setExpanded(true);
        }

        if (mHeaderProj != null) {
            // mHeaderProj.setExpanded(settings.isSlidingProjectExpanded());
            mHeaderProj.setExpanded(true);
        }

        if (mHeaderAvalaibleProj != null) {
            // mHeaderAvalaibleProj.setExpanded(settings.isSlidingAvalaibleProjectExpanded());
            mHeaderAvalaibleProj.setExpanded(true);
        }

        if (mHeaderCat != null) {
            // mHeaderCat.setExpanded(settings.isSlidingCategoryExpanded());
            mHeaderCat.setExpanded(true);
        }
    }

    public void updateProjects(Context context, boolean updateNumber) {
        if (mHeaderProj != null) {
            mItems.remove(mHeaderProj);
        }

        try {
            final List<Project> listProjects = mDbHelper.getProjects(context, updateNumber);

            mHeaderProj = new SlidingMenuHeader(context.getString(R.string.sm_projects), sProjectsRootUUID);

            for (Project project : listProjects) {
                if (project.getParentId() == null) {
                    project.setExpanded(project.isCollapsed());
                    mHeaderProj.getSubnodes().add(project);
                    processProjects(project, listProjects);
                }
            }

            if (!mHeaderProj.getSubnodes().isEmpty()) {
                mItems.add(mHeaderProj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }
    }

    public void updateAvailableProjects(Context context, boolean updateNumber) {
        if (mHeaderAvalaibleProj != null) {
            mItems.remove(mHeaderAvalaibleProj);
        }

        try {
            List<Project> listProjects = mDbHelper.getAvailableProject(context, updateNumber);

            mHeaderAvalaibleProj = new SlidingMenuHeader(context.getString(R.string.sm_available_me),
                    sAvaiableMeRootUUID);
            for (Project project : listProjects) {
                if (project.getParentId() == null) {
                    project.setExpanded(project.isCollapsed());
                    mHeaderAvalaibleProj.getSubnodes().add(project);
                    processProjects(project, listProjects);
                }
            }
            if (!mHeaderAvalaibleProj.getSubnodes().isEmpty()) {
                mItems.add(mHeaderAvalaibleProj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }
    }

    public void updateCategories(Context context, boolean updateNumber, String userName) {
        if (mHeaderCat != null) {
            mItems.remove(mHeaderCat);
        }

        try {
            // TODO Bug #3465 look in "database.getCategories"
            final List<Category> listCategories = mDbHelper.getCategories(context, updateNumber);
            mHeaderCat = new SlidingMenuHeader(context.getString(R.string.sm_categories),
                    CategoriesRootTreeItem.sCategoriesRootUUID);

            for (Category category : listCategories) {
                // TODO Bug #3465 was added "category.isShow() && "
                if (category.isShow() && category.getParentId() == null) {
                    category.setExpanded(category.isCollapsed());
                    mHeaderCat.getSubnodes().add(category);
                    processCategories(category, listCategories);
                }
            }

            // TODO (vsh) change everything before to this line
            // mHeaderCat = new CategoriesRootTreeItem(context.getString(R.string.sm_categories), database, 1, userName,
            // updateNumber);
            if (!mHeaderCat.getSubnodes().isEmpty()) {
                mItems.add(mHeaderCat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }
    }

    public void updateInstructI(Context context, String userName, boolean updateNumber, List<Email> list) {
        if (mHeaderInstructI != null) {
            mItems.remove(mHeaderInstructI);
        }

        try {
            if (list == null) {
                list = mDbHelper.getEmailsInstructI(context, userName, updateNumber);
            }

            if ((list != null) && (list.size() > 0)) {
                mHeaderInstructI = new SlidingMenuHeader(context.getString(R.string.sm_instruct_i), sInstructIRootUUID);
                mHeaderInstructI.getSubnodes().addAll(list);
                mItems.add(mHeaderInstructI);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
            e.printStackTrace();
        }
    }

    public void updateAssignedToMe(Context context, String userName, boolean updateNumber, List<Email> list) {
        if (mHeaderInstructMe != null) {
            mItems.remove(mHeaderInstructMe);
        }

        try {
            if (list == null) {
                if (mDbHelper.getTaskDao() != null) {
                    list = mDbHelper.getEmailsInstructMe(context, updateNumber);
                }
            }

            if (list != null) {
                mHeaderInstructMe = new SlidingMenuHeader(context.getString(R.string.sm_instruct_me),
                        sInstructMeRootUUID);
                mHeaderInstructMe.getSubnodes().addAll(list);
                if (!mHeaderInstructMe.getSubnodes().isEmpty()) {
                    mItems.add(mHeaderInstructMe);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (AbstractDataRequestException e) {
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
        for (Project proj : projects) {
            if (project.getId().equals(proj.getParentId())) {
                project.addChild(proj);
                processProjects(proj, projects);
            }
        }
    }

    /**
     * Create tree hierarchy for categories
     * 
     * @param category
     *            - particulat category
     * @param categories
     *            - all categories
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    private void processCategories(Category category, List<Category> categories) {
        for (Category categ : categories) {
            if (category.getId().equals(categ.getParentId())) {
                category.addChild(categ);
                processCategories(categ, categories);
            }
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
    public List<ITreeData<?>> getSubnodes() {
        return mItems;
    }

    @Override
    public void setExpanded(boolean value) {}
}