package com.ashberrysoft.leadertask.modern.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.BaseLionColumns;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.CategoryTotalLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectLinkContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.ProjectTotalLinkContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.interfaces.CursorFiller;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.utils.Utils;

import static android.R.id.list;
import static com.ashberrysoft.leadertask.R.id.categories;

public class UpdateFeatureLinkHelper {

    private final Context mContext;

    public UpdateFeatureLinkHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public void updateProjectTotalLink(Project project) {
        final ContentValues cv = new ContentValues(3);
        cv.put(ProjectTotalLinkContract.Name, project.getName());
        cv.put(ProjectTotalLinkContract.Showed, project.isShow());
        cv.put(ProjectTotalLinkContract.Shared, project.getSharedUsers()!= null);

        mContext.getContentResolver().update(ProjectTotalLinkContract.CONTENT_URI, //
                cv, SelectionKeeper.eq(null, ProjectTotalLinkContract.Uid,//
                        String.valueOf(project.getId()).toUpperCase()).toString(), null);
    }

    public void createTotalLink(Project project) {
        final ProjectTotalLink last = TaskHelper.getFirstInOrder(mContext,//
                ProjectTotalLink.class, null, ProjectTotalLinkContract.Orders, false);
        final int order = last == null ? 1 : last.getOrder() + 1;

        final ProjectTotalLink link = new ProjectTotalLink();
        link.setUid(String.valueOf(project.getId()).toUpperCase());
        link.setBelongCurrentUser(true);
        link.setOrder(order);
        link.setName(project.getName());
        link.setVisible(true);
        link.setShowed(project.isShow());
        link.setOpened(project.isExpanded());
        link.setShared(project.getSharedUsers()!= null);


        mContext.getContentResolver().insert(ProjectTotalLinkContract.CONTENT_URI, link.getContentValues(null));
    }

    public static <K, V extends CursorFiller> ContentValues[] contentValuesFromMap(Map<K, V> map) {
        final Collection<V> values = map.values();
        final ContentValues[] cvs = new ContentValues[values.size()];
        int count = 0;

        for (V value : values) {
            cvs[count++] = value.getContentValues(null);
        }

        return cvs;
    }

    public static void updateColorTotalLink (Context context, ArrayList <ColorTotalLink> totalLinks) {
        final List<Marker> list = DbHelper.getInstance(context).getAllMarkers();
        for (Marker Color : list) {
            if (Color.getId() != null && Color.getCreator() != null) {
                if (Color.getCreator().equals(LTSettings.getInstance().getUserName())) {
                    // если задача моя
                    boolean isFounded = false;
                    for (ColorTotalLink totalLink : totalLinks) {
                        if (Color.getId().toString().toLowerCase().equals(totalLink.getUid().toLowerCase())) {
                            isFounded = true;
                            break;
                        }
                    }
                    if (!isFounded) {
                        // добавить тотал линк
                        final Map<String, ColorTotalLink> links = new HashMap<>();
                        ColorTotalLink totalLink = null;
                        if (totalLink == null) {
                            try {
                            totalLink = ColorTotalLink.class.newInstance();
                            totalLink.setUid(Color.getId().toString().toUpperCase());
                            totalLink.setName(Color.getName());

                            links.put(totalLink.getUid(), totalLink);
                            final ContentValues[] cvs = contentValuesFromMap(links);

                            context.getContentResolver().bulkInsert(LionMetaData.ColorTotalLinkContract.CONTENT_URI, cvs);

                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }
        }
    }

    public static void updateEmpTotalLinkNew(Context context) {
        try {
            final List<Employee> list = DbHelper.getListEmployees(context);
            if (list.size() > 0) {
                final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

                final StringBuilder sb = new StringBuilder();
                int order = 1;

                for (Employee employee : list) {
                    order = recalculateEmployeeTasks(operations, sb, order, employee);
                }

                context.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    public static void updateEmpTotalLink (Context context, ArrayList <EmpTotalLink> totalLinks) {
        final List<Employee> list = DbHelper.getListEmployees(context);
        for (Employee employee : list) {
            boolean isFounded = false;
            for (EmpTotalLink totalLink : totalLinks) {
                if (employee.getEmail().equals(totalLink.getUid())) {
                    isFounded = true;
                    break;
                }
            }
            if (!isFounded) {
                // добавить тотал линк
                final Map<String, EmpTotalLink> links = new HashMap<>();
                EmpTotalLink totalLink = null;
                if (totalLink == null) {
                    try {
                    totalLink = EmpTotalLink.class.newInstance();
                    totalLink.setUid(employee.getEmail());
                    totalLink.setName(employee.getName());

                    links.put(totalLink.getUid(), totalLink);
                    final ContentValues[] cvs = contentValuesFromMap(links);

                    context.getContentResolver().bulkInsert(LionMetaData.EmpTotalLinkContract.CONTENT_URI, cvs);

                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    public static ColorTotalLink createColorTotal (Context context, Marker Color) {
        final Map<String, ColorTotalLink> links = new HashMap<>();
        ColorTotalLink totalLink = null;
        if (totalLink == null) {
            try {
                totalLink = ColorTotalLink.class.newInstance();
                totalLink.setUid(Color.getId().toString().toUpperCase());
                totalLink.setName(Color.getName());

                links.put(totalLink.getUid(), totalLink);
                final ContentValues[] cvs = contentValuesFromMap(links);

                context.getContentResolver().bulkInsert(LionMetaData.ColorTotalLinkContract.CONTENT_URI, cvs);
                return totalLink;

            } catch (Exception e) {

            }
        }
        return null;
    }

    public static EmpTotalLink createEmpTotal (Context context, Employee Emp) {
        final Map<String, EmpTotalLink> links = new HashMap<>();
        EmpTotalLink totalLink = null;
        if (totalLink == null) {
            try {
                totalLink = EmpTotalLink.class.newInstance();
                totalLink.setUid(Emp.getEmail());
                totalLink.setName(Emp.getName());

                links.put(totalLink.getUid(), totalLink);
                final ContentValues[] cvs = contentValuesFromMap(links);

                context.getContentResolver().bulkInsert(LionMetaData.EmpTotalLinkContract.CONTENT_URI, cvs);
                return totalLink;

            } catch (Exception e) {

            }
        }
        return null;
    }

    public static EmpTotalLink createEmpTotalNew (Context context, Employee Emp) {
        final Map<String, EmpTotalLink> links = new HashMap<>();
        EmpTotalLink totalLink = null;
        if (totalLink == null) {
            try {
                totalLink = EmpTotalLink.class.newInstance();
                totalLink.setUid(Emp.getEmail());
                totalLink.setName(""+ EmployeeCache.getInstance(context).find(Emp.getEmail()));


                links.put(totalLink.getUid(), totalLink);
                final ContentValues[] cvs = contentValuesFromMap(links);

                context.getContentResolver().bulkInsert(LionMetaData.EmpTotalLinkContract.CONTENT_URI, cvs);
                return totalLink;

            } catch (Exception e) {

            }
        }
        return null;
    }

    public static void deleteTotalLink(Context context, Project project) {
        deleteTotalLink(context, String.valueOf(project.getId()).toUpperCase(),//
                ProjectLinkContract.CONTENT_URI, ProjectTotalLinkContract.CONTENT_URI);
    }

    public static void deleteTotalLink(Context context, String uid, Uri link, Uri total) {
        final String selection = SelectionKeeper.equals(null, BaseLionColumns.Uid, uid);

        context.getContentResolver().delete(link, selection, null);
        context.getContentResolver().delete(total, selection, null);
    }

    public static void updateProjectTotalLink(Context context) {
        try {
            final List<Project> projects = new ProjectCategoryTreeHelper(context).getProjectsTree();
            if (projects.size() > 0) {
                final String userName = LTSettings.getInstance().getUserName();
                final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

                final StringBuilder sb = new StringBuilder();
                int order = 1;

                for (Project project : projects) {
                    order = recalculateProjectTasks(operations, sb, //
                            userName, 0, order, project, null);
                }

                context.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private static int recalculateProjectTasks(ArrayList<ContentProviderOperation> operations, StringBuilder sb, //
            String currentUser, int level, int order, Project current, Project parent) {
        final String uid = String.valueOf(current.getId()).toUpperCase();
        {
            final ContentValues update = new ContentValues(14);

            update.put(ProjectTotalLinkContract.Uid, uid);
            update.put(ProjectTotalLinkContract.BelongCurrentUser, currentUser.equals(current.getCreator()));
            update.put(ProjectTotalLinkContract.Orders, order++);
            update.put(ProjectTotalLinkContract.Name, current.getName());
            update.put(ProjectTotalLinkContract.Level, level);
            update.put(ProjectTotalLinkContract.HasBelow, current.isExpandable());
            update.put(ProjectTotalLinkContract.Opened, current.isExpanded());
            update.put(ProjectTotalLinkContract.Visible, !(parent != null && parent.isCollapsed()));
            update.put(ProjectTotalLinkContract.Showed, current.isShow());
            update.put(ProjectTotalLinkContract.Shared, current.getSharedUsers()!=null);

            final ContentValues incert = new ContentValues(update);

            incert.put(ProjectTotalLinkContract.Tasks, 0);
            incert.put(ProjectTotalLinkContract.TasksUnreaded, 0);
            incert.put(ProjectTotalLinkContract.TasksUncompleted, 0);
            incert.put(ProjectTotalLinkContract.TasksUncompletedUnreaded, 0);

            operations.add(ContentProviderOperation.newInsert(ProjectTotalLinkContract.CONTENT_URI).withValues(incert).build());

            Utils.clearStringBuilder(sb);
            operations.add(ContentProviderOperation.newUpdate(ProjectTotalLinkContract.CONTENT_URI).withValues(update)
                    .withSelection(SelectionKeeper.equals(sb, ProjectTotalLinkContract.Uid, uid), null).build());
        }

        if (current.getSubnodes() != null) {
            for (Project project : current.getSubnodes()) {
                order = recalculateProjectTasks(operations, sb, currentUser, level + 1, order, project, current);
            }
        }

        return order;
    }

    public void updateTotalLink(Category category) {
        final ContentValues cv = new ContentValues(2);
        cv.put(CategoryTotalLinkContract.Name, category.getName());
        cv.put(CategoryTotalLinkContract.Showed, category.isShow());

        mContext.getContentResolver().update(CategoryTotalLinkContract.CONTENT_URI, //
                cv, SelectionKeeper.eq(null, CategoryTotalLinkContract.Uid,//
                        String.valueOf(category.getId()).toUpperCase()).toString(), null);
    }

    public void createTotalLink(Category category) {
        final CategoryTotalLink last = TaskHelper.getFirstInOrder(mContext,//
                CategoryTotalLink.class, null, CategoryTotalLinkContract.Orders, false);
        final int order = last == null ? 1 : last.getOrder() + 1;

        final CategoryTotalLink link = new CategoryTotalLink();
        link.setUid(String.valueOf(category.getId()).toUpperCase());
        link.setBelongCurrentUser(true);
        link.setOrder(order);
        link.setName(category.getName());
        link.setVisible(true);
        link.setShowed(category.isShow());
        link.setOpened(category.isExpanded());
        link.setShared(false);

        mContext.getContentResolver().insert(CategoryTotalLinkContract.CONTENT_URI, link.getContentValues(null));
    }

    public void createTotalLink(Marker marker) {
        final ColorTotalLink link = new ColorTotalLink();
        link.setUid(String.valueOf(Marker.FIELD_UID).toUpperCase());
        link.setUid(marker.getId().toString().toUpperCase());
        link.setName(marker.getName());

        mContext.getContentResolver().insert(LionMetaData.ColorTotalLinkContract.CONTENT_URI, link.getContentValues(null));
    }

    public static void deleteTotalLink(Context context, Category category) {
        deleteTotalLink(context, String.valueOf(category.getId()).toUpperCase(),//
                CategoryLinkContract.CONTENT_URI, CategoryTotalLinkContract.CONTENT_URI);
    }

    public static void deleteTotalLink(Context context, Marker marker) {
        deleteTotalLink(context, String.valueOf(marker.getId()).toUpperCase(),//
                LionMetaData.ColorLinkContract.CONTENT_URI, LionMetaData.ColorTotalLinkContract.CONTENT_URI);
    }

    public static void updateCategoryTotalLink(Context context) {
        try {
            final List<Category> categories = new ProjectCategoryTreeHelper(context).getCategoriesTree();
            if (categories.size() > 0) {
                final String currentUser = LTSettings.getInstance().getUserName();
                final ArrayList<ContentProviderOperation> operations = new ArrayList<>();

                final StringBuilder sb = new StringBuilder();
                int order = 1;

                for (Category category : categories) {
                    order = recalculateCategoryTasks(operations, sb, currentUser, 0, order, category, null);
                }

                context.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
            }

        } catch (Exception e) {
            Utils.toLog(e);
        }
    }

    private static int recalculateCategoryTasks(ArrayList<ContentProviderOperation> operations, StringBuilder sb, //
            String currentUser, int level, int order, Category current, Category parent) {
        final String uid = String.valueOf(current.getId()).toUpperCase();
        {
            final ContentValues update = new ContentValues(14);

            update.put(CategoryTotalLinkContract.Uid, uid);
            update.put(CategoryTotalLinkContract.BelongCurrentUser, true);
            update.put(CategoryTotalLinkContract.Orders, order++);
            update.put(CategoryTotalLinkContract.Name, current.getName());
            update.put(CategoryTotalLinkContract.Level, level);
            update.put(CategoryTotalLinkContract.HasBelow, current.isExpandable());
            update.put(CategoryTotalLinkContract.Opened, current.isExpanded());
            update.put(CategoryTotalLinkContract.Visible, !(parent != null && parent.isCollapsed()));
            update.put(CategoryTotalLinkContract.Showed, current.isShow());
            update.put(ProjectTotalLinkContract.Shared, false);

            final ContentValues incert = new ContentValues(update);

            incert.put(CategoryTotalLinkContract.Tasks, 0);
            incert.put(CategoryTotalLinkContract.TasksUnreaded, 0);
            incert.put(CategoryTotalLinkContract.TasksUncompleted, 0);
            incert.put(CategoryTotalLinkContract.TasksUncompletedUnreaded, 0);

            operations.add(ContentProviderOperation.newInsert(CategoryTotalLinkContract.CONTENT_URI).withValues(incert).build());

            Utils.clearStringBuilder(sb);
            operations.add(ContentProviderOperation.newUpdate(CategoryTotalLinkContract.CONTENT_URI).withValues(update)
                    .withSelection(SelectionKeeper.equals(sb, CategoryTotalLinkContract.Uid, uid), null).build());
        }

        if (current.getSubnodes() != null) {
            for (Category category : current.getSubnodes()) {
                order = recalculateCategoryTasks(operations, sb, currentUser, level + 1, order, category, current);
            }
        }

        return order;
    }

    private static int recalculateEmployeeTasks(ArrayList<ContentProviderOperation> operations, StringBuilder sb, int order, Employee current) {
        final String uid = current.getEmail();
        {
            final ContentValues update = new ContentValues(6);

            update.put(LionMetaData.EmpTotalLinkContract.Uid, uid);
//            update.put(LionMetaData.EmpTotalLinkContract.BelongCurrentUser, true);
            update.put(LionMetaData.EmpTotalLinkContract.Orders, order++);
            /*update.put(LionMetaData.EmpTotalLinkContract.Name, current.getName());
            update.put(LionMetaData.EmpTotalLinkContract.Level, level);
            update.put(LionMetaData.EmpTotalLinkContract.HasBelow, current.isExpandable());
            update.put(LionMetaData.EmpTotalLinkContract.Opened, current.isExpanded());
            update.put(LionMetaData.EmpTotalLinkContract.Visible, !(parent != null && parent.isCollapsed()));
            update.put(LionMetaData.EmpTotalLinkContract.Showed, current.isShow());
            update.put(ProjectTotalLinkContract.Shared, false);*/

            final ContentValues incert = new ContentValues(update);

            incert.put(LionMetaData.EmpTotalLinkContract.Tasks, 0);
            incert.put(LionMetaData.EmpTotalLinkContract.TasksUnreaded, 0);
            incert.put(LionMetaData.EmpTotalLinkContract.TasksUncompleted, 0);
            incert.put(LionMetaData.EmpTotalLinkContract.TasksUncompletedUnreaded, 0);

            operations.add(ContentProviderOperation.newInsert(LionMetaData.EmpTotalLinkContract.CONTENT_URI).withValues(incert).build());

            Utils.clearStringBuilder(sb);
            operations.add(ContentProviderOperation.newUpdate(LionMetaData.EmpTotalLinkContract.CONTENT_URI).withValues(update)
                    .withSelection(SelectionKeeper.equals(sb, LionMetaData.EmpTotalLinkContract.Uid, uid), null).build());
        }

        return order;
    }
}