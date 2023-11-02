package com.ashberrysoft.leadertask.modern.xml_handlers.entity_base;

import java.util.List;

import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;

public class ProcessEntityHolder<DATA> extends ErrorEntity {

    private static final long serialVersionUID = 1L;

    private List<String> mDelete;
    private List<String> mSend;
    private List<String> mProcess;
    private List<DATA> mAdd;

    public List<String> getDelete() {
        return mDelete;
    }

    public void setDelete(List<String> delete) {
        mDelete = delete;
    }

    public List<String> getSend() {
        return mSend;
    }

    public void setSend(List<String> send) {
        mSend = send;
    }

    public List<String> getProcess() {
        return mProcess;
    }

    public void setProcess(List<String> process) {
        mProcess = process;
    }

    public List<DATA> getAdd() {
        return mAdd;
    }

    public void setAdd(List<DATA> add) {
        mAdd = add;
    }
}