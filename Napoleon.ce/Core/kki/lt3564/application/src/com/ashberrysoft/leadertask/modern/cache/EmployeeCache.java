package com.ashberrysoft.leadertask.modern.cache;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;

public class EmployeeCache extends BaseMapCache<CharSequence> {

    // SINGLETON
    private static EmployeeCache sInstance;

    public static EmployeeCache getInstance(Context context) {
        if (sInstance == null) {
            synchronized (EmployeeCache.class) {
                if (sInstance == null) {
                    sInstance = new EmployeeCache(context);
                }
            }
        }
        return sInstance;
    }

    protected EmployeeCache(Context context) {
        super(context);
    }

    @Override
    public void refreshCache() {
        clear();
        Cursor c = null;

        try {
            c = getContext().getContentResolver().query(EmployeeContract.CONTENT_URI,//
                    null, null, null, EmployeeContract.DEFAULT_SORT);

            if (c.getCount() > 0) {
                final int columnEmail = c.getColumnIndex(EmployeeContract.EMAIL);
                final int columnName = c.getColumnIndex(EmployeeContract.NAME);

                String email;
                String name;

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    email = c.getString(columnEmail);
                    name = c.getString(columnName);

                    if (check(email, name)) {
                        getCache().put(email, name);
                    }
                }
            }

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        try {
            c = getContext().getContentResolver().query(EmpContract.CONTENT_URI, //
                    null, null, null, EmpContract.DEFAULT_SORT);

            if (c.getCount() > 0) {
                final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);
                final int columnTitle = c.getColumnIndex(EmpContract.TITLE);

                String email;
                String name;

                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    email = c.getString(columnLogin);
                    name = c.getString(columnTitle);

                    if (check(email, name)) {
                        getCache().put(email, name);
                    }
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    private boolean check(String email, String name) {
        return name != null && !name.equals(email);
    }

    @Override
    public String getKey(CharSequence value) {
        return String.valueOf(value);
    }

    @Override
    public CharSequence find(String key) {
        final CharSequence name = super.find(key);
        return name == null ? key : name;
    }
}