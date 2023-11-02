package com.ashberrysoft.leadertask.modern.domains.menu;

import java.util.List;

public interface CollapsibleMenuItem<TYPE extends CollapsibleMenuItem<TYPE>> {

    int getLevel();

    boolean hasBelow();

    boolean isOpened();

    boolean isVisible();

    List<TYPE> getChilds();

    void addChilds(TYPE child);
}