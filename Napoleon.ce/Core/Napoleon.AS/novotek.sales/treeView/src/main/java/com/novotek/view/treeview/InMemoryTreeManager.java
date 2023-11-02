package com.novotek.view.treeview;

import android.database.DataSetObserver;
import android.util.Log;

import com.novotek.view.treeview.exceptions.NodeAlreadyInTreeException;
import com.novotek.view.treeview.exceptions.NodeNotInTreeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryTreeManager implements TreeManager<Long>{
    private static final String TAG = InMemoryTreeManager.class.getSimpleName();
    private static final long serialVersionUID = 1L;
    private final Map<Long, InMemoryTreeNode> allNodes = new HashMap<Long, InMemoryTreeNode>();
    private InMemoryTreeNode topSentinel;
    private transient List<Long> visibleListCache = null; // lasy initialised
    private transient List<Long> unmodifiableVisibleList = null;
    private final transient Set<DataSetObserver> observers = new HashSet<DataSetObserver>();

    private synchronized void internalDataSetChanged() {
        visibleListCache = null;
        unmodifiableVisibleList = null;
        for (final DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }

    public InMemoryTreeManager(InMemoryTreeNode root) {
        topSentinel = root;
        addNodes(topSentinel.getChildren());
    }

    private void addNodes(List<InMemoryTreeNode> children) {
        for(InMemoryTreeNode n : children) {
            allNodes.put(n.getId(), n);
            if(n.getChildren().size() > 0)
                addNodes(n.getChildren());
        }
    }

    public void setRoot(InMemoryTreeNode root) {
        topSentinel = root;
        allNodes.clear();
        addNodes(topSentinel.getChildren());
        refresh();
    }

    public InMemoryTreeNode getNodeFromTreeOrThrow(final Long id) {
        if (id == null) {
            throw new NodeNotInTreeException("(null)");
        }
        final InMemoryTreeNode node = allNodes.get(id);
        if (node == null) {
            throw new NodeNotInTreeException(id.toString());
        }
        return node;
    }

    public InMemoryTreeNode getNodeFromTreeOrThrowAllowRoot(final Long id) {
        if (id == null) {
            return topSentinel;
        }
        return getNodeFromTreeOrThrow(id);
    }

    @Override
    public synchronized TreeNodeInfo<Long> getNodeInfo(final Long id) {
        final InMemoryTreeNode node = getNodeFromTreeOrThrow(id);
        final List<InMemoryTreeNode> children = node.getChildren();
        boolean expanded = false;
        if (!children.isEmpty() && children.get(0).isVisible()) {
            expanded = true;
        }
        return new TreeNodeInfo<Long>(id, getLevel(node.getId()), !children.isEmpty(),
                node.isVisible(), expanded);
    }

    @Override
    public synchronized List<Long> getChildren(final Long id) {
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        return node.getChildIdList();
    }

    @Override
    public synchronized Long getParent(final Long id) {
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        return node.getParent();
    }

    private void setChildrenVisibility(final InMemoryTreeNode node,
                                       final boolean visible, final boolean recursive) {
        for (final InMemoryTreeNode child : node.getChildren()) {
            child.setVisible(visible);
            if (recursive) {
                setChildrenVisibility(child, visible, true);
            }
        }
    }

    @Override
    public synchronized void expandDirectChildren(final Long id) {
        Log.d(TAG, "Expanding direct children of " + id);
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        setChildrenVisibility(node, true, false);
        internalDataSetChanged();
    }

    @Override
    public synchronized void expandEverythingBelow(final Long id) {
        Log.d(TAG, "Expanding all children below " + id);
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        setChildrenVisibility(node, true, true);
        internalDataSetChanged();
    }

    @Override
    public synchronized void collapseChildren(final Long id) {
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        if (node == topSentinel) {
            for (final InMemoryTreeNode n : topSentinel.getChildren()) {
                setChildrenVisibility(n, false, true);
            }
        } else {
            setChildrenVisibility(node, false, true);
        }
        internalDataSetChanged();
    }

    @Override
    public synchronized Long getNextSibling(final Long id) {
        final Long parent = getParent(id);
        final InMemoryTreeNode parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
        boolean returnNext = false;
        for (final InMemoryTreeNode child : parentNode.getChildren()) {
            if (returnNext) {
                return child.getId();
            }
            if (child.getId().equals(id)) {
                returnNext = true;
            }
        }
        return null;
    }

    @Override
    public synchronized Long getPreviousSibling(final Long id) {
        final Long parent = getParent(id);
        final InMemoryTreeNode parentNode = getNodeFromTreeOrThrowAllowRoot(parent);
        Long previousSibling = null;
        for (final InMemoryTreeNode child : parentNode.getChildren()) {
            if (child.getId().equals(id)) {
                return previousSibling;
            }
            previousSibling = child.getId();
        }
        return null;
    }

    @Override
    public synchronized boolean isInTree(final Long id) {
        return allNodes.containsKey(id);
    }

    @Override
    public synchronized int getVisibleCount() {
        return getVisibleList().size();
    }

    @Override
    public synchronized List<Long> getVisibleList() {
        Long currentId = null;
        if (visibleListCache == null) {
            visibleListCache = new ArrayList<Long>(allNodes.size());
            do {
                currentId = getNextVisible(currentId);
                if (currentId == null) {
                    break;
                } else {
                    visibleListCache.add(currentId);
                }
            } while (true);
        }
        if (unmodifiableVisibleList == null) {
            unmodifiableVisibleList = Collections
                    .unmodifiableList(visibleListCache);
        }
        return unmodifiableVisibleList;
    }

    public synchronized Long getNextVisible(final Long id) {
        final InMemoryTreeNode node = getNodeFromTreeOrThrowAllowRoot(id);
        if (!node.isVisible()) {
            return null;
        }
        final List<InMemoryTreeNode> children = node.getChildren();
        if (!children.isEmpty()) {
            final InMemoryTreeNode firstChild = children.get(0);
            if (firstChild.isVisible()) {
                return firstChild.getId();
            }
        }
        final Long sibl = getNextSibling(id);
        if (sibl != null) {
            return sibl;
        }
        Long parent = node.getParent();
        do {
            if (parent == null) {
                return null;
            }
            final Long parentSibling = getNextSibling(parent);
            if (parentSibling != null) {
                return parentSibling;
            }
            parent = getNodeFromTreeOrThrow(parent).getParent();
        } while (true);
    }

    @Override
    public synchronized void registerDataSetObserver(
            final DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public synchronized void unregisterDataSetObserver(
            final DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getLevelCount() {
        int count = 0;
        for(InMemoryTreeNode n : allNodes.values()) {
            if(n.getChildren().size() == 0) {
                int level = getLevel(n.getId());
                if( level > count)
                    count = level;
            }
        }
        return count + 1;
    }

    @Override
    public int getLevel(Long id) {
        int level = -1;
        while(id != null) {
            InMemoryTreeNode n = getNodeFromTreeOrThrow(id);
            id = n.getParent();
            level++;
        }

        return level;
    }

    @Override
    public Integer[] getHierarchyDescription(final Long id) {
        final int level = getLevel(id);
        final Integer[] hierarchy = new Integer[level + 1];
        int currentLevel = level;
        Long currentId = id;
        Long parent = getParent(currentId);
        while (currentLevel >= 0) {
            hierarchy[currentLevel--] = getChildren(parent).indexOf(currentId);
            currentId = parent;
            parent = getParent(parent);
        }
        return hierarchy;
    }

    private void appendToSb(final StringBuilder sb, final Long id) {
        if (id != null) {
            final TreeNodeInfo<Long> node = getNodeInfo(id);
            final int indent = node.getLevel() * 4;
            final char[] indentString = new char[indent];
            Arrays.fill(indentString, ' ');
            sb.append(indentString);
            sb.append(node.toString());
            sb.append(Arrays.asList(getHierarchyDescription(id)).toString());
            sb.append("\n");
        }
        final List<Long> children = getChildren(id);
        for (final Long child : children) {
            appendToSb(sb, child);
        }
    }

    @Override
    public synchronized String toString() {
        final StringBuilder sb = new StringBuilder();
        appendToSb(sb, null);
        return sb.toString();
    }

    @Override
    public synchronized void clear() {
        allNodes.clear();
        topSentinel.clearChildren();
        internalDataSetChanged();
    }

    @Override
    public void refresh() {
        internalDataSetChanged();
    }

}
