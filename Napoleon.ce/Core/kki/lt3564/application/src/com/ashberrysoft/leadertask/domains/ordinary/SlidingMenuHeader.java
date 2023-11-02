package com.ashberrysoft.leadertask.domains.ordinary;

import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.TreeDataContainer;

/**
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class SlidingMenuHeader extends TreeDataContainer<ITreeData<?>> implements SlidingMenuTreeDataContainer {

    private String mName;
    private UUID mId;

    public SlidingMenuHeader(String name, UUID id) {
        mChilds = new ArrayList<ITreeData<?>>(0);
        mName = name;
        mId = id;
    }

    @Override
    public int getNodeLevel() {
        return ETreeDataNodeLevel.HEADER.ordinal();
    }

    @Override
    public boolean isExpandable() {
        return !mChilds.isEmpty();
    }

    @Override
    public int getIndent() {
        return 0;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void saveExpanded(Context context) {
        final LTSettings settings = ((LTApplication) context.getApplicationContext()).getSettings();

        if (mName.equals(context.getString(R.string.sm_instruct_i))) {
            settings.setIsSlidingInstructIExpande(isExpanded);
        }

        else if (mName.equals(context.getString(R.string.sm_instruct_me))) {
            settings.setIsSlidingInstructMyExpande(isExpanded);
        }

        else if (mName.equals(context.getString(R.string.sm_projects))) {
            settings.setIsSlidingProjectExpanded(isExpanded);
        }

        else if (mName.equals(context.getString(R.string.sm_available_me))) {
            settings.setIsSlidingAvalaibleProjectExpanded(isExpanded);
        }

        else if (mName.equals(context.getString(R.string.sm_categories))) {
            settings.setIsSlidingCategoryExpanded(isExpanded);
        }
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public String getFilterId() {
        return mId.toString();
    }
}