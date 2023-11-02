package com.ashberrysoft.leadertask.domains.ordinary;

import java.io.Serializable;
import java.util.List;

import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.v2soft.AndLib.dao.TreeDataContainer;

/**
 * Описывает e-mail.
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 * @since 2014-06-23
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class Email extends TreeDataContainer<Email>//
        implements SlidingMenuTreeDataContainer, Serializable, Comparable<Email> {
    private static final long serialVersionUID = 1L;

    /**
     * Для конторля назначения.
     * 
     * @author "Alexander Slobodchukov (alexander.slobodchukov@gmail.com)"
     * 
     */
    public enum OrderInstruct {
        INSTRUCTI, INSTRUCTME
    }

    private String mEmail;

    private String mTitle;
    private int mOrders;

    private List<Task> mTasks;
    private OrderInstruct mOrderInstruct;

    public Email(String str, OrderInstruct orderInstruct) {
        mOrderInstruct = orderInstruct;
        setName(str);
    }

    public Email(String str, boolean instructI) {
        mOrderInstruct = instructI ? OrderInstruct.INSTRUCTI : OrderInstruct.INSTRUCTME;
        setName(str);
    }

    @Override
    public int getNodeLevel() {
        switch (mOrderInstruct) {
        case INSTRUCTI:
            return ETreeDataNodeLevel.INSTRUCTI.ordinal();
        case INSTRUCTME:
            return ETreeDataNodeLevel.INSTRUCTME.ordinal();
        }
        return -1;
    }

    @Override
    public boolean isExpandable() {
        return false;
    }

    @Override
    public int getIndent() {
        return 0;
    }

    @Override
    public String getName() {
        return mEmail;
    }

    public void setName(String name) {
        this.mEmail = name;
    }

    public List<Task> getTasks() {
        return mTasks;
    }

    public void setTasks(List<Task> mTasks) {
        this.mTasks = mTasks;
    }

    public OrderInstruct getOrderInstruct() {
        return mOrderInstruct;
    }

    public void setOrderInstruct(OrderInstruct mOrderInstruct) {
        this.mOrderInstruct = mOrderInstruct;
    }

    @Override
    public String getFilterId() {
        return mEmail;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getOrders() {
        return mOrders;
    }

    public void setOrders(int orders) {
        mOrders = orders;
    }

    @Override
    public int compareTo(Email email) {
        if (this.getOrders() > email.getOrders()) {
            return 1;
        } else if (this.getOrders() < email.getOrders()) {
            return -1;
        }
        return 0;
    }
}