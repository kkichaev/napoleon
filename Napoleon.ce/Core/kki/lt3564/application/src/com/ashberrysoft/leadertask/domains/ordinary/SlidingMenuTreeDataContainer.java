package com.ashberrysoft.leadertask.domains.ordinary;

/**
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * 
 * @param <T>
 */
public interface SlidingMenuTreeDataContainer {

    public String getName();

    public String getFilterId();

    /**
     * 
     * @return indent level from 0 to n. 0 means that it's a top level item.
     */
    public abstract int getIndent();

}
