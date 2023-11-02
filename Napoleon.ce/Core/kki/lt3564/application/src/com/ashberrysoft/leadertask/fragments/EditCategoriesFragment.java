package com.ashberrysoft.leadertask.fragments;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.adapters.SimpleFeatureListAdapter;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.UidToDeleteContract;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.UidToDelete;
import com.ashberrysoft.leadertask.modern.helper.UpdateFeatureLinkHelper;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.ListItemSimpleFeatureView.OnSimpleFeatureViewListener;
import com.software.shell.fab.ActionButton;
import com.v2soft.AndLib.dao.ITreePureNode;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EditCategoriesFragment extends BaseFeaturesFragment implements OnSimpleFeatureViewListener<ITreePureNode> {

    private static final String CLASS_PATH = EditCategoriesFragment.class.getName();
    private static final String EXTRA_CATEGORY = CLASS_PATH + "EXTRA_CATEGORY";

    // VALUE's
    private MenuInflater mMenuInflater;
    private Category mTempCategory;
    private int mTempPosition;
    // ADAPTER
    private SimpleFeatureListAdapter<ITreePureNode> mAdapter;

    public static EditCategoriesFragment newInstance() {
        return new EditCategoriesFragment();
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        mTempCategory = b != null ? ((Category) b.getSerializable(EXTRA_CATEGORY)) : null;
        mMenuInflater = getActivity().getMenuInflater();
        mAdapter = new SimpleFeatureListAdapter<ITreePureNode>(getActivity(), FeatureType.CATEGORY, this);
        setActionButtonListener();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter.setData(SimpleFeatureListAdapter.getListCategories(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable(EXTRA_CATEGORY, mTempCategory);
    }

    @Override
    public void onStop() {
        //mAdapter.clear();
        super.onStop();
    }

    @Override
    public void onSimpleFeatureViewClick(ITreePureNode data) {
        openCategory((Category) data);
    }

    @Override
    public void onSimpleFeatureViewLongClick(View v, ITreePureNode data, int position, ITreePureNode dataPrev, ITreePureNode dataPost) {
        mTempCategory = (Category) data;
        mTempPosition = position;
        getActivity().openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (mTempCategory == null) {
            return;
        }

        mMenuInflater.inflate(R.menu.edit_feature_contextmenu, menu);
        if (mTempCategory.getParent() == null) {
            setMenuForRoot(menu);
        } else {
            setMenuForChild(menu);
        }
    }

    private void setMenuForRoot(ContextMenu menu) {
        setMenuItemEnabled(menu.findItem(R.id.m_go_left), false);
        if (mTempPosition == 0) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }

        if (mTempPosition == mAdapter.getData().size() - 1 - recursiveChildsCount(mTempCategory)) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }
    }

    private void setMenuForChild(ContextMenu menu) {
        final Category parent = mTempCategory.getParent();
        final List<Category> childs = parent.getSubnodes();

        int position;
        for (position = 0; position < childs.size(); position++) {
            if (mTempCategory.getId().equals(childs.get(position).getId())) {
                break;
            }
        }

        if (position == 0) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_up), false);
            setMenuItemEnabled(menu.findItem(R.id.m_go_right), false);
        }

        if (position == childs.size() - 1) {
            setMenuItemEnabled(menu.findItem(R.id.m_go_down), false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_properties:
            openCategory(mTempCategory);
            return true;

        case R.id.m_go_left:
            setBlockAtUI(true);
            new Thread(mLeftRun).start();
            return true;

        case R.id.m_go_right:
            setBlockAtUI(true);
            new Thread(mRightRun).start();
            return true;

        case R.id.m_go_up:
            setBlockAtUI(true);
            new Thread(mUpRun).start();
            return true;

        case R.id.m_go_down:
            setBlockAtUI(true);
            new Thread(mDownRun).start();
            return true;

        case R.id.menu_dell:
            showSimpleDialog(R.string.d_category_remove_title, R.string.d_category_remove_message);
            return true;

        default:
            return super.onContextItemSelected(item);
        }
    }

    private final Runnable mLeftRun = new Runnable() {
        @Override
        public void run() {
            goLeft();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private final Runnable mRightRun = new Runnable() {
        @Override
        public void run() {
            try {
                goRight();
            } catch (IndexOutOfBoundsException e) {
                Utils.toLog(e);
            }

            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private final Runnable mUpRun = new Runnable() {
        @Override
        public void run() {
            goUp();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private final Runnable mDownRun = new Runnable() {
        @Override
        public void run() {
            goDown();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private void updateAdapterData() {
        UpdateFeatureLinkHelper.updateCategoryTotalLink(mApp);

        mAdapter.setData(SimpleFeatureListAdapter.getListCategories(mSettings, mDbHelper));
        adapterNotifyDataSetChanged();
    }

    private void goLeft() {
        final Category parent = mTempCategory.getParent();
        final Category parentParent = parent.getParent();

        final List<Category> categories;
        if (parentParent == null) {
            categories = new ArrayList<Category>();
            for (ITreePureNode i : mAdapter.getData()) {
                final Category p = (Category) i;
                if (p.getParentId() == null) {
                    categories.add(p);
                }
            }
        } else {
            categories = parentParent.getSubnodes();
        }

        int parentPosition = -1;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(parent.getId())) {
                parentPosition = i;
                break;
            }
        }

        mTempCategory.setParentId(parent.getParentId());
        mTempCategory.setUsnParent(mTempCategory.getUsnParent() + 1);
        categories.add(parentPosition + 1, mTempCategory);

        for (int i = 0; i < categories.size(); i++) {
            final Category p = categories.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        mDbHelper.updateCategories(categories);
    }

    private void goRight() {
        Category newParent = null;

        {
            final int tempCategoryIndent = mTempCategory.getIndent();
            int newParentPos = mTempPosition - 1;
            while ((newParent = (Category) mAdapter.getData().get(newParentPos)).getIndent() != tempCategoryIndent) {
                newParentPos--;
            }
        }

        final Category oldParent = mTempCategory.getParent();
        mTempCategory.setParentId(newParent.getId());
        mTempCategory.setUsnParent(mTempCategory.getUsnParent() + 1);

        final List<Category> childs = newParent.getSubnodes();
        childs.add(mTempCategory);

        for (int i = 0; i < childs.size(); i++) {
            final Category p = childs.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        updateOrdersToIndent(oldParent);
        mDbHelper.updateCategories(childs);
    }

    private void goUp() {
        int newParentPosition = mTempPosition - 1;
        Category neighbourhood = null;
        while ((neighbourhood = (Category) mAdapter.getData().get(newParentPosition)).getIndent() != mTempCategory.getIndent()) {
            newParentPosition--;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempCategory.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

        mTempCategory.setOrder(neighbourhoodOrder);
        mTempCategory.setUsnPlusPlus();
        mTempCategory.setUsnOrder(mTempCategory.getUsnOrder() + 1);

        final List<Category> categories = new ArrayList<Category>(2);
        categories.add(neighbourhood);
        categories.add(mTempCategory);

        mDbHelper.updateCategories(categories);
    }

    private void goDown() {
        int newParentPosition = mTempPosition + 1;
        Category neighbourhood = null;
        while ((neighbourhood = (Category) mAdapter.getData().get(newParentPosition)).getIndent() != mTempCategory.getIndent()) {
            newParentPosition++;
        }

        final int neighbourhoodOrder = neighbourhood.getOrder();
        neighbourhood.setOrder(mTempCategory.getOrder());
        neighbourhood.setUsnPlusPlus();
        neighbourhood.setUsnOrder(neighbourhood.getUsnOrder() + 1);

        mTempCategory.setOrder(neighbourhoodOrder);
        mTempCategory.setUsnPlusPlus();
        mTempCategory.setUsnOrder(mTempCategory.getUsnOrder() + 1);

        final List<Category> categories = new ArrayList<Category>(2);
        categories.add(neighbourhood);
        categories.add(mTempCategory);

        mDbHelper.updateCategories(categories);
    }

    private void updateOrdersToIndent(Category oldParent) {
        final List<Category> categories;
        if (oldParent == null) {
            categories = new ArrayList<Category>();
            for (ITreePureNode i : mAdapter.getData()) {
                final Category p = (Category) i;
                if (p.getParentId() == null) {
                    categories.add(p);
                }
            }
        } else {
            categories = oldParent.getSubnodes();
        }

        categories.remove(mTempCategory);

        for (int i = 0; i < categories.size(); i++) {
            final Category p = categories.get(i);
            p.setUsnPlusPlus();
            p.setOrder(i + 1);
            p.setUsnOrder(p.getUsnOrder() + 1);
        }

        mDbHelper.updateCategories(categories);
    }

    @Override
    protected void onDialogPositiveButton() {
        setBlockAtUI(true);
        new Thread(mRemoveRun).start();
    }

    private final Runnable mRemoveRun = new Runnable() {
        @Override
        public void run() {
            removeFeature();
            updateAdapterData();
            setBlockAtUI(false);
        }
    };

    private void removeFeature() {
        try {
            updateOrdersToIndent(mTempCategory.getParent());

            mApp.getContentResolver().insert(UidToDeleteContract.CONTENT_URI, UidToDelete.getContentValues(mTempCategory));

            mDbHelper.getCategoryDao().delete(mTempCategory);


            mApp.getContentResolver().notifyChange(LionMetaData.LTaskContract.CONTENT_URI, null);

        } catch (SQLException e) {
            Utils.toLog(e);
        }

        UpdateFeatureLinkHelper.deleteTotalLink(mApp, mTempCategory);

        mTempCategory = null;
    }

    private void openCategory(Category category) {
        FeaturesActivity.hideActionButton();
        startFragment(PropertiesCategoryFragment.newInstance(category));
    }

    @Override
    protected boolean onAddFeatureClick() {
        openCategory(null);
        return true;
    }

    @Override
    protected View getListViewHeader() {
        return null;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.task_category;
    }

    @Override
    protected int getActionBarIcon() {
        return R.drawable.category_white_big;
    }

    @Override
    protected boolean getVisibilitySwitchMode() {
        return true;
    }

    @Override
    protected boolean onSaveFeatureClick() {
        return false;
    }

    @Override
    protected boolean onOtherFeatureClick(MenuItem item) {
        return false;
    }

    private int recursiveChildsCount(Category parent) {
        int count = 0;
        if (parent == null || parent.getSubnodes() == null || parent.getSubnodes().isEmpty()) {
            return count;
        }

        for (Category child : parent.getSubnodes()) {
            count++;
            count += recursiveChildsCount(child);
        }
        return count;
    }

    @Override
    protected boolean runOperationInBackground(Operation operation) {
        return false;
    }

    private void setActionButtonListener(){
        FeaturesActivity.mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddFeatureClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        FeaturesActivity.showActionButton();
    }
}