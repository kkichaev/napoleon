package com.ashberrysoft.leadertask.domains.ordinary;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * Корневой элемент для дерева категорий.
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class CategoriesRootTreeItem //
        implements ITreeData<ITreeData<Category>>, SlidingMenuTreeDataContainer {

    public static final UUID sCategoriesRootUUID = UUID.fromString("ae0fa485-5981-4742-95a3-48ada7accdd1");
    private Context mContext;
    private List<ITreeData<Category>> mItems;
    private int mNodeLevel;
    private String mName;
    private boolean mUpdate;

    public CategoriesRootTreeItem(Context context, DbHelper database, boolean update) {
        mContext = context;
        mNodeLevel = 0;
        mUpdate = update;

        try {
            updateCategories(database);

        } catch (SQLException e) {
            mItems = new ArrayList<ITreeData<Category>>(0);

        } catch (AbstractDataRequestException e) {
            mItems = new ArrayList<ITreeData<Category>>(0);
        }
    }

    public void updateCategories(DbHelper database) throws SQLException, AbstractDataRequestException {
        final List<Category> listCategories = database.getCategories(mContext, mUpdate);
        mItems = new ArrayList<ITreeData<Category>>();

        for (Category category : listCategories) {
            if (category.getParentId() == null) {
                category.setExpanded(category.isCollapsed());
                mItems.add(category);
                processCategories(category, listCategories);
            }
        }
    }

    /**
     * Create tree hierarchy for categories
     * 
     * @param category
     *            - particulat category
     * @param categories
     *            - all categories
     * 
     * @author Vadim Oleynik (vadim.welldone@gmail.com)
     */
    private void processCategories(Category category, List<Category> categories) {
        for (Category categ : categories) {
            if (category.getId().equals(categ.getParentId())) {// TODO:
                category.addChild(categ);
                processCategories(categ, categories);
            }
        }
    }

    @Override
    public int getNodeLevel() {
        return mNodeLevel;
    }

    @Override
    public boolean isExpandable() {
        return true;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

    @Override
    public int getChildsCount() {
        return mItems.size();
    }

    @Override
    public List<ITreeData<Category>> getSubnodes() {
        return mItems;
    }

    @Override
    public void setExpanded(boolean value) {}

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public int getIndent() {
        return 0;
    }

    @Override
    public String getFilterId() {
        return sCategoriesRootUUID.toString();
    }
}
