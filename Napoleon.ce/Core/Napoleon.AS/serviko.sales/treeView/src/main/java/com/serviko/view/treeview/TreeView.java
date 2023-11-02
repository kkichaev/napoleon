package com.serviko.view.treeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.serviko.view.treeview.exceptions.TreeConfigurationException;

public class TreeView extends ListView {
    private static final int DEFAULT_COLLAPSED_RESOURCE = R.drawable.collapsed;
    private static final int DEFAULT_EXPANDED_RESOURCE = R.drawable.expanded;
    private static final int DEFAULT_INDENT = 0;
    private static final int DEFAULT_GRAVITY = Gravity.LEFT
            | Gravity.CENTER_VERTICAL;
    private Drawable expandedDrawable;
    private Drawable collapsedDrawable;
    private Drawable rowBackgroundDrawable;
    private Drawable indicatorBackgroundDrawable;
    private int indentWidth = 0;
    private int indicatorGravity = 0;
    private AbstractTreeViewAdapter< ? > treeAdapter;
    private boolean collapsable;
    private boolean handleTrackballPress;

    public TreeView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.style.treeViewStyle);
    }

    public TreeView(final Context context) {
        this(context, null);
    }

    public TreeView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TreeView);
        expandedDrawable = a.getDrawable(R.styleable.TreeView_src_expanded);
        if (expandedDrawable == null) {
            expandedDrawable = context.getResources().getDrawable(
                    DEFAULT_EXPANDED_RESOURCE);
        }
        collapsedDrawable = a
                .getDrawable(R.styleable.TreeView_src_collapsed);
        if (collapsedDrawable == null) {
            collapsedDrawable = context.getResources().getDrawable(
                    DEFAULT_COLLAPSED_RESOURCE);
        }
        indentWidth = a.getDimensionPixelSize(
                R.styleable.TreeView_indent_width, DEFAULT_INDENT);
        indicatorGravity = a.getInteger(
                R.styleable.TreeView_indicator_gravity, DEFAULT_GRAVITY);
        indicatorBackgroundDrawable = a
                .getDrawable(R.styleable.TreeView_indicator_background);
        rowBackgroundDrawable = a
                .getDrawable(R.styleable.TreeView_row_background);
        collapsable = a.getBoolean(R.styleable.TreeView_collapsable, true);
        handleTrackballPress = a.getBoolean(
                R.styleable.TreeView_handle_trackball_press, true);
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof AbstractTreeViewAdapter)) {
            throw new TreeConfigurationException(
                    "The adapter is not of TreeViewAdapter type");
        }
        treeAdapter = (AbstractTreeViewAdapter< ? >) adapter;
        syncAdapter();
        super.setAdapter(treeAdapter);
    }

    private void syncAdapter() {
        treeAdapter.setCollapsedDrawable(collapsedDrawable);
        treeAdapter.setExpandedDrawable(expandedDrawable);
        treeAdapter.setIndicatorGravity(indicatorGravity);
        treeAdapter.setIndentWidth(indentWidth);
        treeAdapter.setIndicatorBackgroundDrawable(indicatorBackgroundDrawable);
        treeAdapter.setRowBackgroundDrawable(rowBackgroundDrawable);
        treeAdapter.setCollapsable(collapsable);
        if (handleTrackballPress) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView< ? > parent,
                                        final View view, final int position, final long id) {
                    treeAdapter.handleItemClick(view, view.getTag());
                }
            });
        } else {
            setOnClickListener(null);
        }

    }

    public void setExpandedDrawable(final Drawable expandedDrawable) {
        this.expandedDrawable = expandedDrawable;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setCollapsedDrawable(final Drawable collapsedDrawable) {
        this.collapsedDrawable = collapsedDrawable;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setRowBackgroundDrawable(final Drawable rowBackgroundDrawable) {
        this.rowBackgroundDrawable = rowBackgroundDrawable;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setIndicatorBackgroundDrawable(
            final Drawable indicatorBackgroundDrawable) {
        this.indicatorBackgroundDrawable = indicatorBackgroundDrawable;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setIndentWidth(final int indentWidth) {
        this.indentWidth = indentWidth;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setIndicatorGravity(final int indicatorGravity) {
        this.indicatorGravity = indicatorGravity;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setCollapsable(final boolean collapsable) {
        this.collapsable = collapsable;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setHandleTrackballPress(final boolean handleTrackballPress) {
        this.handleTrackballPress = handleTrackballPress;
        syncAdapter();
        treeAdapter.refresh();
    }

    public Drawable getExpandedDrawable() {
        return expandedDrawable;
    }

    public Drawable getCollapsedDrawable() {
        return collapsedDrawable;
    }

    public Drawable getRowBackgroundDrawable() {
        return rowBackgroundDrawable;
    }

    public Drawable getIndicatorBackgroundDrawable() {
        return indicatorBackgroundDrawable;
    }

    public int getIndentWidth() {
        return indentWidth;
    }

    public int getIndicatorGravity() {
        return indicatorGravity;
    }

    public boolean isCollapsable() {
        return collapsable;
    }

    public boolean isHandleTrackballPress() {
        return handleTrackballPress;
    }
}
