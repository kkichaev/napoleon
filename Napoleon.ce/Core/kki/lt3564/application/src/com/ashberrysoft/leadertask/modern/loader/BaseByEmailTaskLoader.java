package com.ashberrysoft.leadertask.modern.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.domains.link.BaseCollapsibleTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.BaseTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ByMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.EmpTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ForMeTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.drawable.cursor;

public class BaseByEmailTaskLoader extends BaseLTaskLoader {

    public interface OnEmailWithEmployeesLoaderListener {

        void onByMeLinksLoad(List<ByMeTotalLink> links);

        void onEmpLinksLoad(List<EmpTotalLink> links);

        void onForMeLinksLoad(List<ForMeTotalLink> links);
    }

    // BASE
    private final OnEmailWithEmployeesLoaderListener mListener;

    // VALUE's
    private List<ByMeTotalLink> mByMeLinks;
    private List<EmpTotalLink> mEmpLinks;
    private List<ForMeTotalLink> mForMeLinks;

    protected BaseByEmailTaskLoader(Context context, //
            Uri contentUri, String selection, String order) {
        this(context, null, null, contentUri, selection, order);
    }

    protected BaseByEmailTaskLoader(Context context, OnEmailWithEmployeesLoaderListener listener, BaseMenuItem menuItem,//
            Uri contentUri, String selection, String order) {
        super(context, menuItem, contentUri, selection, order);
        mListener = listener;
    }

    @Override
    public Cursor loadInBackground() {
        if (mListener != null) {
            final Cursor cursor = super.loadInBackground();

            if (getMenuItem().getMenuItemType() == MenuItemType.BY_ME) {
                onByMeTaskLoad(cursor);

            } else {
                if (getMenuItem().getMenuItemType() == MenuItemType.EMP) {
                    onEmpTaskLoad(cursor);
                    //onEmpTaskLoadNew(cursor);
                } else {
                    onForMeTaskLoad(cursor);
                }
            }

            return cursor;

        } else {
            return super.loadInBackground();
        }
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (mListener != null) {
            if (getMenuItem().getMenuItemType() == MenuItemType.BY_ME) {
                mListener.onByMeLinksLoad(mByMeLinks);

            } else {
                if (getMenuItem().getMenuItemType() == MenuItemType.EMP) {
                    mListener.onEmpLinksLoad(mEmpLinks);
                }else {
                    mListener.onForMeLinksLoad(mForMeLinks);
                }
            }
        }

        super.deliverResult(cursor);
    }

    private void onByMeTaskLoad(Cursor cursor) {
        if (cursor.getCount() == 0) {
            mByMeLinks = new ArrayList<>(0);
            return;
        }

        final List<ByMeTotalLink> tasks = new ArrayList<>(cursor.getCount());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tasks.add(new ByMeTotalLink(cursor));
        }

        clearListFromEmptyItems(tasks, getSettings().isMakeTaskHide());
        final List<ByMeTotalLink> finded = getFindedList(getEmployees(), tasks);
        filterUnfindedList(getContext(), tasks);

        finded.addAll(tasks);
        mByMeLinks = finded;
    }

    private void onEmpTaskLoadNew(Cursor cursor) {
        mEmpLinks = getLinkListNew(cursor, EmpTotalLink.class);
    }
    private void onEmpTaskLoad(Cursor cursor) {
        if (cursor.getCount() == 0) {
            mEmpLinks = new ArrayList<>(0);
            return;
        }

        final List<EmpTotalLink> tasks = new ArrayList<>(cursor.getCount());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tasks.add(new EmpTotalLink(cursor));
        }

        final List <EmpTotalLink> finded = getFindedList(getEmployees(), tasks);
        filterUnfindedList(getContext(), tasks);

        finded.addAll(tasks);
        mEmpLinks = finded;
    }

    private void onForMeTaskLoad(Cursor cursor) {
        if (cursor.getCount() == 0) {
            mForMeLinks = new ArrayList<>(0);
            return;
        }

        final List<ForMeTotalLink> tasks = new ArrayList<>(cursor.getCount());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            tasks.add(new ForMeTotalLink(cursor));
        }

        clearListFromEmptyItems(tasks, getSettings().isMakeTaskHide());
        final List<ForMeTotalLink> finded = getFindedList(getEmployees(), tasks);
        filterUnfindedList(getContext(), tasks);

        finded.addAll(tasks);
        mForMeLinks = finded;
    }

    private static <T extends BaseTotalLink> List<T> getFindedList(List<SimpleEmployee> employees, List<T> values) {
        final List<T> finded = new ArrayList<>();
        {
            T task;
            for (SimpleEmployee employee : employees) {
                for (Iterator<T> iterator = values.iterator(); iterator.hasNext();) {
                    task = iterator.next();

                    if (employee.email != null){
                        if (employee.email.equalsIgnoreCase(task.getUid())) {
                            task.setName(employee.title);
                            finded.add(task);
                            iterator.remove();
                            break;
                        }
                    }
                }
            }
        }

        return finded;
    }

    private static <T extends BaseTotalLink> void filterUnfindedList(Context context, List<T> values) {
        if (values.size() > 0) {
            final EmployeeCache employeeCache = EmployeeCache.getInstance(context);

            for (T task : values) {
                task.setName(String.valueOf(employeeCache.find(task.getUid())));
            }
        }
    }

    private static <T extends BaseMenuItem> void clearListFromEmptyItems(List<T> items, boolean showUncompleted) {
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext();) {
            final T item = iterator.next();

            if (showUncompleted) {
                if (item.getTasksUncompleted() == 0) {
                    iterator.remove();
                }

            } else if (item.getTasks() == 0) {
                iterator.remove();
            }
        }
    }

    private List<SimpleEmployee> getEmployees() {
        Cursor c = null;
        try {
            c = getContext().getContentResolver().query(EmpContract.CONTENT_URI, null, null, null, EmpContract.DEFAULT_SORT);

            final List<SimpleEmployee> employees = new ArrayList<>(c.getCount());
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                employees.add(new SimpleEmployee(c));
            }
            return employees;

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private static final class SimpleEmployee {

        public final String email;
        public final String title;

        private static int[] sColumns;

        public SimpleEmployee(Cursor c) {
            if (sColumns == null) {
                sColumns = new int[2];

                sColumns[0] = c.getColumnIndex(EmpContract.LOGIN);
                sColumns[1] = c.getColumnIndex(EmpContract.TITLE);
            }

            email = c.getString(sColumns[0]);
            title = c.getString(sColumns[1]);
        }
    }

    private static List <EmpTotalLink> getLinkListNew(Cursor cursor, Class<EmpTotalLink> cls) {
        final List<EmpTotalLink> values = new ArrayList<>(cursor.getCount());

        if (cursor.getCount() > 0) {
            EmpTotalLink value;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                try {
                    value = cls.newInstance();
                    value.fillFromCursor(cursor);

                    values.add(value);

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }

            return getCollapsibleTasksNew(values);
        }

        return values;
    }

    private static List <EmpTotalLink> getCollapsibleTasksNew(List<EmpTotalLink> values) {
        final List<EmpTotalLink> result = new ArrayList<>(values.size());
        for (EmpTotalLink value : values) {
            result.add(value);
        }
        return result;
    }
}