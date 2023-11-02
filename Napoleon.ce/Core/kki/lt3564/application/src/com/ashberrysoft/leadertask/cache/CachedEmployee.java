package com.ashberrysoft.leadertask.cache;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;

/**
 * 
 * @since 2014-06-23
 * @author Tregub Artem tregub.artem@gmail.com
 */
@Deprecated
public class CachedEmployee {

    // INSTANCE
    private static volatile CachedEmployee sInstance;

    // VALUE's
    private Context mContext;
    private HashMap<String, String> mEmployees;

    public static CachedEmployee getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CachedEmployee.class) {
                if (sInstance == null) {
                    sInstance = new CachedEmployee(context);
                }
            }
        }
        return sInstance;
    }

    private CachedEmployee(Context context) {
        mContext = context.getApplicationContext();
        mEmployees = new HashMap<String, String>();
    }

    /** Fill Employees HasMap with all possible existing Employees */
    public void update() {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        Cursor c = null;

        try {
            c = mContext.getContentResolver().query(EmployeeContract.CONTENT_URI,//
                    null, null, null, EmployeeContract.DEFAULT_SORT);
            final int columnEmail = c.getColumnIndex(EmployeeContract.EMAIL);
            final int columnName = c.getColumnIndex(EmployeeContract.NAME);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                final String email = c.getString(columnEmail);
                final String name = c.getString(columnName);

                if (!TextUtils.isEmpty(name) && !name.equals(email)) {
                    hashMap.put(email, name);
                }
            }

        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
        }

        try {
            c = mContext.getContentResolver()
                    .query(EmpContract.CONTENT_URI, null, null, null, EmpContract.DEFAULT_SORT);
            final int columnLogin = c.getColumnIndex(EmpContract.LOGIN);
            final int columnTitle = c.getColumnIndex(EmpContract.TITLE);

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                final String email = c.getString(columnLogin);
                final String name = c.getString(columnTitle);

                if (!TextUtils.isEmpty(name) && !name.equals(email)) {
                    hashMap.put(email, name);
                }
            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        mEmployees.clear();
        mEmployees.putAll(hashMap);
    }

    /** Return cute name if exist, or email if name not exist */
    public String getName(String email) {
        final String name = mEmployees.get(email);
        return name != null ? name : email;
    }
}