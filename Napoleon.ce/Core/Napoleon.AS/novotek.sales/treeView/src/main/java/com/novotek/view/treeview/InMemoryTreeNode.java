package com.novotek.view.treeview;

import android.view.View;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTreeNode implements Serializable {
    private static final long serialVersionUID = 1L;
    static long lastId = 1;

    private final Long id;
    private Long parent;
    private boolean visible = true;
    private final List<InMemoryTreeNode> children = new LinkedList<InMemoryTreeNode>();
    private List<Long> childIdListCache = null;

    protected InMemoryTreeNode(final boolean visible) {
        super();
        this.id = lastId++;
        this.parent = null;
        this.visible = visible;
    }

    private InMemoryTreeNode() {
        id = null;
        this.parent = null;
        this.visible = true;
    }

    public void bindTo(InMemoryTreeNode parent) {
        this.parent = parent.getId();
    }

    public static InMemoryTreeNode createRoot() {
        return new InMemoryTreeNode();
    }

    public int getLayoutID() { return 0; }
    public void updateView(View view, boolean expanded) {}

    public int indexOf(final Long id) {
        return getChildIdList().indexOf(id);
    }

    /**
     * Cache is built lasily only if needed. The cache is cleaned on any
     * structure change for that node!).
     *
     * @return list of ids of children
     */
    public synchronized List<Long> getChildIdList() {
        if (childIdListCache == null) {
            childIdListCache = new LinkedList<Long>();
            for (final InMemoryTreeNode n : children) {
                childIdListCache.add(n.getId());
            }
        }
        return childIdListCache;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
    }

    public int getChildrenListSize() {
        return children.size();
    }

    public synchronized void add(InMemoryTreeNode node) {
        childIdListCache = null;
        node.bindTo(this);
        children.add(node);
    }

    public synchronized <T extends InMemoryTreeNode> void addAll(List<T> nodes) {
        childIdListCache = null;

        for(InMemoryTreeNode node : nodes) {
            node.bindTo(this);
        }
        children.addAll(nodes);
    }

    public synchronized void remove(InMemoryTreeNode node) {
        children.remove(node);
        childIdListCache = null;
    }

    /**
     * Note. This method should technically return unmodifiable collection, but
     * for performance reason on small devices we do not do it.
     *
     * @return children list
     */
    public List<InMemoryTreeNode> getChildren() {
        return children;
    }

    public synchronized void clearChildren() {
        children.clear();
        childIdListCache = null;
    }

    public synchronized void removeChild(final Long child) {
        final int childIndex = indexOf(child);
        if (childIndex != -1) {
            children.remove(childIndex);
            childIdListCache = null;
        }
    }

    @Override
    public String toString() {
        return "InMemoryTreeNode [id=" + getId() + ", parent=" + getParent()
                + ", visible=" + visible
                + ", children=" + children + ", childIdListCache="
                + childIdListCache + "]";
    }

    Long getId() {
        return id;
    }

    Long getParent() {
        return parent;
    }
}