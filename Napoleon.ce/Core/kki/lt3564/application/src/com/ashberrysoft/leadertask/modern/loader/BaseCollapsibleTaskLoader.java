package com.ashberrysoft.leadertask.modern.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.modern.domains.link.BaseCollapsibleTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.CategoryTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ColorTotalLink;
import com.ashberrysoft.leadertask.modern.domains.link.ProjectTotalLink;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.utils.Utils;

public class BaseCollapsibleTaskLoader extends BaseLTaskLoader {

    public interface OnCollapsibleTaskLoaderListener {

        void onProjectLinksLoad(List<ProjectTotalLink> links);

        void onProjectLinksLoadAvailable(List<ProjectTotalLink> links);

        void onCategoryLinksLoad(List<CategoryTotalLink> links);

        void onColorLinksLoad(List<ColorTotalLink> links);
    }

    // BASE
    private final OnCollapsibleTaskLoaderListener mListener;

    // VALUE's
    private List<ProjectTotalLink> mProjectLinks;
    private List<CategoryTotalLink> mCategoryLinks;
    private List<ColorTotalLink> mColorLinks;

    protected BaseCollapsibleTaskLoader(Context context, //
            Uri contentUri, String selection, String order) {
        this(context, null, null, contentUri, selection, order);
    }

    protected BaseCollapsibleTaskLoader(Context context, OnCollapsibleTaskLoaderListener listener,
            BaseMenuItem menuItem,//
            Uri contentUri, String selection, String order) {
        super(context, menuItem, contentUri, selection, order);
        mListener = listener;
    }

    @Override
    public Cursor loadInBackground() {
        if (mListener != null) {
            final Cursor cursor = super.loadInBackground();

            switch (getMenuItem().getMenuItemType()) {
            case PROJECTS:
            case PROJECTS_SHARED:
            case AVAILABLE_PROJECTS:
                mProjectLinks = getLinkList(cursor, ProjectTotalLink.class);
                break;

            case CATEGORIES:
                mCategoryLinks = getLinkList(cursor, CategoryTotalLink.class);
                break;

            case COLOR:
                mColorLinks = getLinkListNew(cursor, ColorTotalLink.class);
                break;

            default:
                break;
            }

            return cursor;

        } else {
            return super.loadInBackground();
        }
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (mListener != null) {
            switch (getMenuItem().getMenuItemType()) {
            case PROJECTS:
//                android.util.Log.v("Tedorius","PROJECTS");
                mListener.onProjectLinksLoad(mProjectLinks);
                break;
            case PROJECTS_SHARED:
//                android.util.Log.v("Tedorius","PROJECTS_SHARED");
                mListener.onProjectLinksLoad(mProjectLinks);
                break;
            case AVAILABLE_PROJECTS:
//                android.util.Log.v("Tedorius","AVAILABLE_PROJECTS");
                mListener.onProjectLinksLoadAvailable(mProjectLinks);
                break;

            case CATEGORIES:
//                android.util.Log.v("Tedorius","CATEGORIES");
                mListener.onCategoryLinksLoad(mCategoryLinks);
                break;

            case COLOR:
                mListener.onColorLinksLoad(mColorLinks);
                break;

            default:
                break;
            }
        }
        if (cursor != null) {
            super.deliverResult(cursor);
        }
    }

    private static List <ColorTotalLink> getLinkListNew(Cursor cursor, Class<ColorTotalLink> cls) {
        final List<ColorTotalLink> values = new ArrayList<>(cursor.getCount());

        if (cursor.getCount() > 0) {
            ColorTotalLink value;
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

    private static <T extends BaseCollapsibleTotalLink<T>> List<T> getLinkList(Cursor cursor, Class<T> cls) {
        final List<T> values = new ArrayList<>(cursor.getCount());

        if (cursor.getCount() > 0) {
            T value;
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                try {
                    value = cls.newInstance();
                    value.fillFromCursor(cursor);

                    values.add(value);

                } catch (Exception e) {
                    Utils.toLog(e);
                }
            }

            setCollapsibleTaskChilds(values);
            return getCollapsibleTasks(values);
        }

        return values;
    }

    private static <T extends BaseCollapsibleTotalLink<T>> void setCollapsibleTaskChilds(List<T> values) {
        for (Iterator<T> iterator = values.iterator(); iterator.hasNext();) {
            final T value = iterator.next();
            if (value.getLevel() == 0 && value.getName() != null) {
                setCollapsibleTaskChilds(values, value);

            } else {
                iterator.remove();
            }
        }
    }

    private static <T extends BaseCollapsibleTotalLink<T>> void setCollapsibleTaskChilds(List<T> values, T current) {
        boolean start = false;

        for (T value : values) {
            if (start) {
                final int nextLevel = current.getLevel() + 1;
                if (value.getLevel() == nextLevel /*&& value.isShowed()*/) {
                    current.addChilds(value);
                    setCollapsibleTaskChilds(values, value);

                } else if (value.getLevel() < nextLevel) {
                    break;
                }

            } else if (value == current) {
                start = true;
            }
        }
    }

    private static List <ColorTotalLink> getCollapsibleTasksNew(List<ColorTotalLink> values) {
        final List<ColorTotalLink> result = new ArrayList<>(values.size());
        for (ColorTotalLink value : values) {
            result.add(value);
        }
        return result;
    }

    private static <T extends BaseCollapsibleTotalLink<T>> List<T> getCollapsibleTasks(List<T> values) {
        final List<T> result = new ArrayList<>(values.size());
        for (T value : values) {
            result.add(value);
            if (value.isOpened()) {
                if (value.getChilds() == null) {
                    value.setHasBelow(false);

                } else {
                    addCollapsibleTasks(result, value.getChilds());
                }
            }
        }
        return result;
    }

    private static <T extends BaseCollapsibleTotalLink<T>> void addCollapsibleTasks(List<T> result, List<T> values) {
        for (T value : values) {
            result.add(value);
            if (value.isOpened()) {
                if (value.getChilds() == null) {
                    value.setHasBelow(false);

                } else {
                    addCollapsibleTasks(result, value.getChilds());
                }
            }
        }
    }
}