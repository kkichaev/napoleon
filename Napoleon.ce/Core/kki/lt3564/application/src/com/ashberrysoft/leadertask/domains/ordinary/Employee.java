package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.UUID;

import android.content.ContentValues;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.xml_handlers.BaseLionEntityInterface;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentMimeTypeVnd;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation.DefaultContentUri;

@DatabaseTable(tableName = EmployeeContract.TABLE_NAME)
@DefaultContentUri(authority = LeaderTaskProviderMetaData.AUTHORITY, path = EmployeeContract.TABLE_NAME)
@DefaultContentMimeTypeVnd(name = LeaderTaskProviderMetaData.AUTHORITY_PROVIDER, type = EmployeeContract.TABLE_NAME)
public class Employee//
        implements Serializable, BaseLionEntityInterface, Comparable<Employee> {

    private static final long serialVersionUID = 1L;

    @DatabaseField(columnName = EmployeeContract._ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = EmployeeContract.NAME)
    private String mName;

    @DatabaseField(columnName = EmployeeContract.EMAIL)
    private String mEmail;

    private String mInvite;

    private String mPhone;

    private static int[] sColumns;

    public Employee() {}

    public Employee(Cursor c) {
        setData(c);
    }

    public Employee(String email, String name) {
        setEmail(email);
        setName(name);
    }

    public void setData(Cursor c) {
        filleFastColumns(c);

        mId = c.getInt(sColumns[0]);
        mName = c.getString(sColumns[1]);
        mEmail = c.getString(sColumns[2]);
    }

    private static void filleFastColumns(Cursor c) {
        if (sColumns == null) {
            synchronized (Employee.class) {
                if (sColumns == null) {
                    sColumns = new int[3];

                    sColumns[0] = c.getColumnIndex(EmployeeContract._ID);
                    sColumns[1] = c.getColumnIndex(EmployeeContract.NAME);
                    sColumns[2] = c.getColumnIndex(EmployeeContract.EMAIL);
                }
            }
        }
    }

    @Override
    public void fillKeyValue(String key, String value) {
        if (EmployeeContract.NAME.equalsIgnoreCase(key)) {
            setName(value);
            return;
        }

        if (EmployeeContract.EMAIL.equalsIgnoreCase(key)) {
            setEmail(value);
            return;
        }

        if ("invite".equalsIgnoreCase(key)) {
            setInvite(value);
            return;
        }

        if ("phone".equalsIgnoreCase(key)) {
            setPhone(value);
            return;
        }
    }

    @Override
    public void getLionEntity(StringBuilder sb) {}

    @Override
    public String getServerClass() {
        return EmployeeContract.SERVER_CLASS;
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public int getIdTask() {
        return 0;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getInvite() {
        return mInvite;
    }

    public void setInvite(String invite) {
        mInvite = invite;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getTableId() {
        return mId;
    }

    @Override
    public ContentValues getContentValues(ContentValues cv) {
        if (cv == null) {
            cv = new ContentValues(2);
        }

        cv.put(EmployeeContract.NAME, mName);
        cv.put(EmployeeContract.EMAIL, mEmail);

        return cv;
    }

    @Override
    public int compareTo(Employee another) {
        return getEmail().compareTo(another.getEmail());
    }
}