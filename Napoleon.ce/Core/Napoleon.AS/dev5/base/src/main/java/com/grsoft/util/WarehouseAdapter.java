package com.grsoft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.grsoft.aceteam.R;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;

public abstract class WarehouseAdapter extends BaseAdapter implements
		TreeNodeFactory {
	public WarehouseManager warehouse;
	protected OnChangeListener onChangeListener;
	protected FolderTreeNode root;
	protected FolderTreeNode folderTop;
	protected FolderTreeNode priceTop;
	protected List<Filter> filters = new ArrayList<Filter>();
	protected boolean solidPrice = false;
	@SuppressLint("UseSparseArrays")
	protected Map<Integer, ArrayList<PriceInfo>> fprice = new HashMap<Integer, ArrayList<PriceInfo>>();
	private BuildSetThread buildSetThread;
	boolean refreshing = false;

	public class PriceInfo {
		public long rowid;
		public String name;
		public String id;

		public PriceInfo(long rowid, String name, String id) {
			this.rowid = rowid;
			this.name = name;
			this.id = id;
		}
	}

	public abstract String getName();

	public interface OnChangeListener {
		void startBuildSet(WarehouseAdapter adapter);

		void endBuildSet(WarehouseAdapter adapter);

		void onAdapterChange(WarehouseAdapter adapter);

		void setSelection(int position);
	}

	public WarehouseAdapter(WarehouseManager warehouse) {
		this.warehouse = warehouse;
		root = createFoldersTreeNode(null);
		folderTop = root;
		priceTop = createFoldersTreeNode(null);
	}
	
	public FolderTreeNode getRootNode() {
		return solidPrice ? priceTop : folderTop;
	}

	@Override
	public int getCount() {
		if (refreshing)
			return 0;

		FolderTreeNode ftn = getRootNode();
		int result = 0;

		if (ftn == null || ftn.getChilds() == null)
			result = 0;
		else
			result = ftn.getChilds().size();

		return result;
	}

	@Override
	public Object getItem(int position) {
		FolderTreeNode ftn = getRootNode();
		int childPos = position < ftn.getChildsCount() ? position : ftn.getChildsCount() - 1;
		return ftn.getChild(childPos);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		TreeNode node = (TreeNode) getItem(arg0);
		View view = node.isFolderNode() ? warehouse.getFolderView((FolderTreeNode) node, convertView) : 
			warehouse.getPriceView((PriceTreeNode) node, convertView);

		if (view != null && warehouse.useInterlaceBackground())
			view.setBackgroundResource(arg0 % 2 != 0 ? R.drawable.even_row_selector
					: R.drawable.list_selector);
		
		postUpdateView(view, node);
		return view;
	}

	protected void postUpdateView(View view, TreeNode node) {}

	public void buildSet(boolean solidPrice) {
		this.solidPrice = solidPrice;
		buildSet();
	}

	public void buildSet() {
		buildSet(-1);
	}

	public void buildSet(int id) {
		buildSetThread = BuildSetThread.createInstance(this);

		if (buildSetThread != null) {
			buildSetThread.topFolderId = id;
			buildSetThread.execute((Void[]) null);
		}
	}

	@Override
	public String getWhereStr() {
		StringBuilder result = new StringBuilder();

		for (Filter f : filters) {
			String where = f.getWhereStr();
			if (where.length() > 0) {
				if (result.length() != 0)
					result.append(" AND ");
				result.append('(').append(where).append(')');
			}
		}

		return result.toString();
	}
	
	public boolean inset(long rowid, String id, int folder) {
		return inset(rowid, id);
	}
	
	public boolean inset(long rowid, String id) {
		for (Filter f : filters)
			if (!f.inset(rowid, id))
				return false;

		return true;
	}

	protected FolderTreeNode createFoldersTreeNode(FolderTreeNode parent) {
		return new FolderTreeNode(this, parent);
	}

	@Override
	public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id) {
		return new PriceTreeNode(root, priceRowId, name, id);
	}

	public void setOnChangeListener(OnChangeListener listener) {
		onChangeListener = listener;
	}

	protected void fireStartBuildSet() {
		refreshing = true;
		if (onChangeListener != null)
			onChangeListener.startBuildSet(this);
	}

	protected void fireSetSelection(int pos) {
		if (onChangeListener != null)
			onChangeListener.setSelection(pos);
	}

	protected void fireEndBuildSet(final int folderid) {
		refreshing = false;
		setFolder(folderid);

		if (onChangeListener != null)
			onChangeListener.endBuildSet(this);
	}

	public void close() {
		if (buildSetThread != null)
			buildSetThread.cancel(true);
	}

	public void onClick(int pos) {
		TreeNode node = (TreeNode) getItem(pos);

		if (node != null) {
			node.open();

			boolean resetFolderPos = false;
			if (node.isFolderNode()) {
				resetFolderPos = true;
				folderTop = (FolderTreeNode) node;
			} else
				warehouse.editItem(node.getRowid());

			fireDataSetChanged();
			if( resetFolderPos )
				fireSetSelection(0);
		}
	}

	public boolean findFolder(int folderId) {
		FolderTreeNode n = root.findFolder(folderId);
		if (n != null) {
			folderTop = n;
			return true;
		}
		return false;
	}

	public void setFolder(int folderid) {
		FolderTreeNode node = folderTop;

		if (findFolder(folderid)) {
			folderTop.reload();
		} else
			folderTop = root;

		int pos = -1;

		while (node != null && node != folderTop) {
			pos = folderTop.indexOf(node);

			if (pos != -1)
				break;
			node = (FolderTreeNode) node.getParent();
		}

		fireDataSetChanged();
		fireSetSelection(pos);
	}

	public void fireDataSetChanged() {
		notifyDataSetChanged();

		if (onChangeListener != null)
			onChangeListener.onAdapterChange(this);
	}

	public boolean isTop() {
		return folderTop == root;
	}

	public FolderTreeNode getFolderTop() {
		return folderTop;
	}

	public CharSequence getTitle() {
		return isTop() ? warehouse.getString(R.string.price) : folderTop
				.toString();
	}

	public List<TreeNode> path() {
		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		FolderTreeNode node = folderTop;

		do {
			list.add(0, node);
			node = (FolderTreeNode) node.getParent();
		} while (node != null && node.getParent() != null);

		return list;

	}

	public void upLevel() {
	}

	public void nextFolder() {
	}

	public void prevFolder() {
	}

	public Filter getFilter(String name) {
		Filter result = null;

		for (Filter f : filters)
			if (f.getName().equals(name)) {
				result = f;
				break;
			}

		return result;
	}

	public boolean isExpanded() {
		return solidPrice;
	}

	public void expandSwitch() {
		folderTop = root;
		solidPrice = !solidPrice;
		buildSet();
	}

	public void putFilter(Filter filter) {
		if(filter != null) {
			deleteFilter(filter.getName());
			filters.add(filter);
		}
	}

	public void deleteFilter(String name) {
		for (Filter f : filters)
			if (f.getName().equals(name)) {
				filters.remove(f);
				break;
			}
	}

	public abstract void buldProcess(AsyncTask<?, ?, ?> task);

	public String getFoldersIds() {
		if (!isTop() && folderTop instanceof FolderTreeNode)
			return ((FolderTreeNode) folderTop).getFoldersIds();
		else
			return "";
	}

	public List<PriceInfo> getPriceInfo(int folderid) {
		if (fprice.containsKey(folderid))
			return fprice.get(folderid);

		return null;
	}

	public void setExpanded(boolean expand) {
		solidPrice = expand;
	}

	public void copyFilters(WarehouseAdapter adapter) {
		filters.clear();
		filters.addAll(adapter.filters);
	}
}