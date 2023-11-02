package com.ashberrysoft.leadertask.adapters;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.enums.ETreeDataNodeLevel;
import com.ashberrysoft.leadertask.views.CategoryListItem;
import com.ashberrysoft.leadertask.views.CategoryListItem.OnCategoryListItemListener;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.adapters.TreeAdapter;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Класс, предназначенный для формирования элементов-категорий, которые будут представлены в диалоге выбора категории
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class CategoryAdapter extends TreeAdapter implements OnCategoryListItemListener {

    static CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>> sFactory = new CustomViewAdapterFactory<ITreePureNode, IDataView<ITreePureNode>>() {
        @Override
        public IDataView<ITreePureNode> createView(Context context, int type) {
            return new CategoryListItem(context);
        }
    };

    // VALUE's
    private Set<Category> mSelectedCategories;
    private DbHelper mDbHelper;

    // private boolean mIsCollapseExpand = true;

    public CategoryAdapter(Context context, ITreeData<?> root, Set<Category> selectedCategories) {
        super(context, root, sFactory);

        mDbHelper = DbHelper.getInstance(mContext);

        if (selectedCategories == null) {
            selectedCategories = new HashSet<Category>(0);
        }
        mSelectedCategories = selectedCategories;
    }

    @Override
    public int getViewTypeCount() {
        return ETreeDataNodeLevel.CATEGORY.ordinal() + 1;
    }

    @Override
    public View getView(int position, View cV, ViewGroup parent) {
        final CategoryListItem v = cV == null ? new CategoryListItem(mContext, this) : (CategoryListItem) cV;
        final ITreePureNode item = (ITreePureNode) getItem(position);
        v.setData(item);
        v.setChecked(mSelectedCategories.contains(item));

        return v;
    }

    /**
     * Return selected categories set.
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @return selected categories set.
     */
    public Set<Category> getSelectedCategories() {
        return mSelectedCategories;
    }

    @Override
    public void onCategoryChecked(Category category, boolean isChecked) {
        if (isChecked) {
            mSelectedCategories.add(category);

        } else {
            mSelectedCategories.remove(category);
        }
    }

    @Override
    public void onCategoryOpen(Category category) {
        mDbHelper.setCategoryCollapsed(category, !category.isCollapsed());// TODO: categoryLink
        notifyDataSetChanged();
    }
}
