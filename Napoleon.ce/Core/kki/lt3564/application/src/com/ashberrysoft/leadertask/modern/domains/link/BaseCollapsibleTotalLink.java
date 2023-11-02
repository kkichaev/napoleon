package com.ashberrysoft.leadertask.modern.domains.link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.CollapsibleTotalLinkContract;

public abstract class BaseCollapsibleTotalLink<C extends BaseCollapsibleTotalLink<C>> extends BaseTotalLink {

    private static final long serialVersionUID = 1L;
    private static final Map<Uri, Integer[]> COLUMNS = new HashMap<>(2);

    private List<C> mChilds;

    public BaseCollapsibleTotalLink() {}

    public BaseCollapsibleTotalLink(Cursor cursor) {
        super(cursor);
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        cv = super.getContentValues(cv);

        cv.put(CollapsibleTotalLinkContract.Name, getName());
        cv.put(CollapsibleTotalLinkContract.Orders, getOrder());
        cv.put(CollapsibleTotalLinkContract.BelongCurrentUser, isBelongCurrentUser());
        cv.put(CollapsibleTotalLinkContract.Level, getLevel());
        cv.put(CollapsibleTotalLinkContract.HasBelow, hasBelow());
        cv.put(CollapsibleTotalLinkContract.Opened, isOpened());
        cv.put(CollapsibleTotalLinkContract.Visible, isVisible());
        cv.put(CollapsibleTotalLinkContract.Showed, isShowed());
        cv.put(CollapsibleTotalLinkContract.Shared, isShared());

        return cv;
    }

    @Override
    public void fillFromCursor(Cursor cursor) {
        super.fillFromCursor(cursor);

        Integer[] columns = COLUMNS.get(getContentUri());
        if (columns == null) {
            columns = new Integer[9];

            columns[0] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Name);
            columns[1] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Orders);
            columns[2] = cursor.getColumnIndex(CollapsibleTotalLinkContract.BelongCurrentUser);
            columns[3] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Level);
            columns[4] = cursor.getColumnIndex(CollapsibleTotalLinkContract.HasBelow);
            columns[5] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Opened);
            columns[6] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Visible);
            columns[7] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Showed);
            columns[8] = cursor.getColumnIndex(CollapsibleTotalLinkContract.Shared);

            COLUMNS.put(getContentUri(), columns);
        }

        setName(cursor.getString(columns[0]));
        setOrder(cursor.getInt(columns[1]));
        setBelongCurrentUser(cursor.getInt(columns[2]) == 1);
        setLevel(cursor.getInt(columns[3]));
        setHasBelow(cursor.getInt(columns[4]) == 1);
        setOpened(cursor.getInt(columns[5]) == 1);
        setVisible(cursor.getInt(columns[6]) == 1);
        setShowed(cursor.getInt(columns[7]) == 1);
        setShared(cursor.getInt(columns[8]) == 1);
    }

    public abstract void setOrder(int order);

    public abstract boolean isBelongCurrentUser();

    public abstract void setBelongCurrentUser(boolean belongCurrentUser);

    public abstract boolean isShared();

    public abstract void setShared(boolean shared);

    public abstract void setLevel(int level);

    public abstract void setHasBelow(boolean hasBelow);

    public abstract boolean isShowed();

    public abstract void setShowed(boolean showed);

    public List<C> getChilds() {
        return mChilds;
    }

    public void addChilds(C child) {
        if (mChilds == null) {
            mChilds = new ArrayList<>();
        }
        mChilds.add(child);
    }
}