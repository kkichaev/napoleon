package com.ashberrysoft.leadertask.data_providers;

import java.io.Serializable;
import java.sql.SQLException;

import android.content.Context;

import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.service.LeaderTaskService;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AbstractServiceRequest;

/**
 * Базовый класс для запросов к локальной БД.
 * 
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 * @param <R>
 */
public abstract class BaseDatabaseRequest<R extends Serializable> extends AbstractServiceRequest<R, Void, R> {
    private static final long serialVersionUID = 1L;
    protected transient DbHelper mDbHelper;

    public BaseDatabaseRequest(Context context) {
        super(context);
    }

    @Override
    protected String getServiceAction() {
        return ServiceConstants.RECIVE;
    }

    @Override
    protected Class<?> getServiceClass() {
        return LeaderTaskService.class;
    }

    @Override
    protected R parseResult(R data) throws AbstractDataRequestException {
        return data;
    }

    @Override
    protected Void prepareParameters() throws AbstractDataRequestException {
        mDbHelper = DbHelper.getInstance(mContext);
        return null;
    }

    /**
     * Обновление записи о количестве задач в разделе.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param fnt
     * @throws SQLException
     */
    protected void updateFilterNumberTaskRecord(FilterNumberTask fnt) throws SQLException {
        FilterNumberTask oldRecord = mDbHelper.getFilterNumberTaskDao().queryBuilder().where()
                .eq(FilterNumberTask.FIELD_NAME, fnt.getName()).and()
                .eq(FilterNumberTask.FIELD_TASK_MODE, fnt.getTaskMode()).queryForFirst();
        if (oldRecord != null) {
            fnt.setId(oldRecord.getId());
            mDbHelper.getFilterNumberTaskDao().update(fnt);
        } else {
            mDbHelper.getFilterNumberTaskDao().create(fnt);
        }
    }
}