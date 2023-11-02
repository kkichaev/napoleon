package com.novotek.view.treeview;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class TreeAdapter extends AbstractTreeViewAdapter<Long> {

    public TreeAdapter(Context context, InMemoryTreeNode root) {
        super(context, new InMemoryTreeManager(root));
    }

    @Override
    public View getNewChildView(TreeNodeInfo<Long> treeNodeInfo) {
        InMemoryTreeNode node = ((InMemoryTreeManager)treeManager).getNodeFromTreeOrThrow(treeNodeInfo.getId());
        View ret = View.inflate(context, node.getLayoutID(), null);
        return updateView(ret, treeNodeInfo);
    }

    @Override
    public View updateView(View view, TreeNodeInfo<Long> treeNodeInfo) {
        InMemoryTreeNode node = ((InMemoryTreeManager)treeManager).getNodeFromTreeOrThrow(treeNodeInfo.getId());
        node.updateView(view, treeNodeInfo.isExpanded());
        return view;
    }

    @Override public long getItemId(int position) { return getTreeId(position); }
}
