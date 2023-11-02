package com.ashberrysoft.leadertask.modern.xml_handlers.entity_base;

import java.util.List;

import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;

public class PutEntityHolder<DATA> extends ErrorEntity {

    private static final long serialVersionUID = 1L;

    private List<DATA> mChange;
    private List<String> mFailed;
    private List<String> mDelete;

    public List<DATA> getChange() {
        return mChange;
    }

    public void setChange(List<DATA> change) {
        mChange = change;
    }

    public List<String> getFailed() {
        return mFailed;
    }

    public void setFailed(List<String> failed) {
        mFailed = failed;
    }

    public List<String> getDelete() {
        return mDelete;
    }

    public void setDelete(List<String> delete) {
        mDelete = delete;
    }
}