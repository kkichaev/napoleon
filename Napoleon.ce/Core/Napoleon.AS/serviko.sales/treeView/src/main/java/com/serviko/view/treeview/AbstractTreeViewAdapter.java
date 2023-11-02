package com.serviko.view.treeview;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public abstract class AbstractTreeViewAdapter<T> extends BaseAdapter implements ListAdapter {
    private static final String TAG = AbstractTreeViewAdapter.class.getSimpleName();
    protected final TreeManager<T> treeManager;
    private final LayoutInflater layoutInflater;

    private int indentWidth = 0;
    private int indicatorGravity = 0;
    private Drawable collapsedDrawable;
    private Drawable expandedDrawable;
    private Drawable indicatorBackgroundDrawable;
    private Drawable rowBackgroundDrawable;

    private final View.OnClickListener indicatorClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            @SuppressWarnings("unchecked")
            final T id = (T) v.getTag();
            expandCollapse(id);
        }
    };

    private boolean collapsable;
    protected final Context context;

    protected void expandCollapse(final T id) {
        final TreeNodeInfo<T> info = treeManager.getNodeInfo(id);
        if (!info.isWithChildren()) {
            // ignore - no default action
            return;
        }
        if (info.isExpanded()) {
            treeManager.collapseChildren(id);
        } else {
            treeManager.expandDirectChildren(id);
        }
    }

    private void calculateIndentWidth() {
        if (expandedDrawable != null) {
            indentWidth = Math.max(getIndentWidth(),
                    expandedDrawable.getIntrinsicWidth());
        }
        if (collapsedDrawable != null) {
            indentWidth = Math.max(getIndentWidth(),
                    collapsedDrawable.getIntrinsicWidth());
        }
    }

    public AbstractTreeViewAdapter(final Context context, final TreeManager<T> treeManager) {
        this.context = context;
        this.treeManager = treeManager;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.collapsedDrawable = null;
        this.expandedDrawable = null;
        this.rowBackgroundDrawable = null;
        this.indicatorBackgroundDrawable = null;
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        treeManager.registerDataSetObserver(observer);
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver observer) {
        treeManager.unregisterDataSetObserver(observer);
        super.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return treeManager.getVisibleCount();
    }

    @Override
    public Object getItem(final int position) {
        return getTreeId(position);
    }

    public T getTreeId(final int position) {
        return treeManager.getVisibleList().get(position);
    }

    public TreeNodeInfo<T> getTreeNodeInfo(final int position) {
        return treeManager.getNodeInfo(getTreeId(position));
    }

    @Override
    public boolean hasStableIds() { // NOPMD
        return true;
    }

    @Override
    public int getItemViewType(final int position) {
        return getTreeNodeInfo(position).getLevel();
    }

    @Override
    public int getViewTypeCount() {
        return treeManager.getLevelCount();
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public boolean areAllItemsEnabled() { // NOPMD
        return true;
    }

    @Override
    public boolean isEnabled(final int position) { // NOPMD
        return true;
    }

    protected int getTreeListItemWrapperId() {
        return R.layout.tree_list_item_wrapper;
    }

    @Override
    public final View getView(final int position, final View convertView,
                              final ViewGroup parent) {
        Log.d(TAG, "Creating a view based on " + convertView
                + " with position " + position);
        final TreeNodeInfo<T> nodeInfo = getTreeNodeInfo(position);
        if (convertView == null) {
            Log.d(TAG, "Creating the view a new");
            final LinearLayout layout = (LinearLayout) layoutInflater.inflate(
                    getTreeListItemWrapperId(), null);
            return populateTreeItem(layout, getNewChildView(nodeInfo),
                    nodeInfo, true);
        } else {
            Log.d(TAG, "Reusing the view");
            final LinearLayout linear = (LinearLayout) convertView;
            final FrameLayout frameLayout = (FrameLayout) linear
                    .findViewById(R.id.treeview_list_item_frame);
            final View childView = frameLayout.getChildAt(0);
            updateView(childView, nodeInfo);
            return populateTreeItem(linear, childView, nodeInfo, false);
        }
    }

    /**
     * Called when new view is to be created.
     *
     * @param treeNodeInfo
     *            node info
     * @return view that should be displayed as tree content
     */
    public abstract View getNewChildView(TreeNodeInfo<T> treeNodeInfo);

    /**
     * Called when new view is going to be reused. You should update the view
     * and fill it in with the data required to display the new information. You
     * can also create a new view, which will mean that the old view will not be
     * reused.
     *
     * @param view
     *            view that should be updated with the new values
     * @param treeNodeInfo
     *            node info used to populate the view
     * @return view to used as row indented content
     */
    public abstract View updateView(View view, TreeNodeInfo<T> treeNodeInfo);

    /**
     * Retrieves background drawable for the node.
     *
     * @param treeNodeInfo
     *            node info
     * @return drawable returned as background for the whole row. Might be null,
     *         then default background is used
     */
    public Drawable getBackgroundDrawable(final TreeNodeInfo<T> treeNodeInfo) { // NOPMD
        return null;
    }

    private Drawable getDrawableOrDefaultBackground(final Drawable r) {
        if (r == null) {
            return context.getResources().getDrawable(R.drawable.list_selector_background).mutate();
        } else {
            return r;
        }
    }

    public final LinearLayout populateTreeItem(final LinearLayout layout,
                                               final View childView, final TreeNodeInfo<T> nodeInfo,
                                               final boolean newChildView) {
        final Drawable individualRowDrawable = getBackgroundDrawable(nodeInfo);
        layout.setBackgroundDrawable(individualRowDrawable == null ? getDrawableOrDefaultBackground(rowBackgroundDrawable)
                : individualRowDrawable);
        final LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
                calculateIndentation(nodeInfo), LinearLayout.LayoutParams.FILL_PARENT);
        final LinearLayout indicatorLayout = (LinearLayout) layout
                .findViewById(R.id.treeview_list_item_image_layout);
        indicatorLayout.setGravity(indicatorGravity);
        indicatorLayout.setPadding(calculateIndentation(nodeInfo) - getIndentWidth(), 0, 0, 0);
        indicatorLayout.setLayoutParams(indicatorLayoutParams);
        final ImageView image = (ImageView) layout
                .findViewById(R.id.treeview_list_item_image);
        image.setImageDrawable(getDrawable(nodeInfo));
        image.setBackgroundDrawable(getDrawableOrDefaultBackground(indicatorBackgroundDrawable));
        image.setScaleType(ImageView.ScaleType.CENTER);
        image.setTag(nodeInfo.getId());
        if (nodeInfo.isWithChildren() && collapsable) {
            image.setOnClickListener(indicatorClickListener);
        } else {
            image.setOnClickListener(null);
        }
        layout.setTag(nodeInfo.getId());
        final FrameLayout frameLayout = (FrameLayout) layout
                .findViewById(R.id.treeview_list_item_frame);
        final FrameLayout.LayoutParams childParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        if (newChildView) {
            frameLayout.addView(childView, childParams);
        }
        frameLayout.setTag(nodeInfo.getId());
        return layout;
    }

    protected int calculateIndentation(final TreeNodeInfo<T> nodeInfo) {
        return getIndentWidth() * (nodeInfo.getLevel() + (collapsable ? 1 : 0));
    }

    protected Drawable getDrawable(final TreeNodeInfo<T> nodeInfo) {
        if (!nodeInfo.isWithChildren() || !collapsable) {
            return getDrawableOrDefaultBackground(indicatorBackgroundDrawable);
        }
        if (nodeInfo.isExpanded()) {
            return expandedDrawable;
        } else {
            return collapsedDrawable;
        }
    }

    public void setIndicatorGravity(final int indicatorGravity) {
        this.indicatorGravity = indicatorGravity;
    }

    public void setCollapsedDrawable(final Drawable collapsedDrawable) {
        this.collapsedDrawable = collapsedDrawable;
        calculateIndentWidth();
    }

    public void setExpandedDrawable(final Drawable expandedDrawable) {
        this.expandedDrawable = expandedDrawable;
        calculateIndentWidth();
    }

    public void setIndentWidth(final int indentWidth) {
        this.indentWidth = indentWidth;
        calculateIndentWidth();
    }

    public void setRowBackgroundDrawable(final Drawable rowBackgroundDrawable) {
        this.rowBackgroundDrawable = rowBackgroundDrawable;
    }

    public void setIndicatorBackgroundDrawable(
            final Drawable indicatorBackgroundDrawable) {
        this.indicatorBackgroundDrawable = indicatorBackgroundDrawable;
    }

    public void setCollapsable(final boolean collapsable) {
        this.collapsable = collapsable;
    }

    public void refresh() {
        treeManager.refresh();
    }

    private int getIndentWidth() {
        return indentWidth;
    }

    @SuppressWarnings("unchecked")
    public void handleItemClick(final View view, final Object id) {
        expandCollapse((T) id);
    }

}