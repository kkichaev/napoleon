/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Дерево FolderTreeNode
 *
 * kki   27/11/2010   creating
 */
package com.grsoft.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.view.View;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseAdapter.PriceInfo;

public class FolderTreeNode extends TreeNode
{
	public int id;
	public int level;
	public String name;
	public boolean priceLoaded;
	protected boolean nodeIsLeaf;
	private boolean useZeroFilter;
	protected TreeNodeFactory foldersTree;
	
	public FolderTreeNode(TreeNodeFactory foldersTree, FolderTreeNode parent)
	{
		super(parent);
		this.foldersTree = foldersTree;
	}
	
	public void insert(TreeNode child)
	{
		child.setParent(this);
		getChilds().add(child);
	}

	@Override
	@Deprecated
	/***
	 * open()
	 */
	public void onClick(View v)
	{
		boolean newValueZeroFilter = (v == null) ? useZeroFilter : (Boolean) v.getTag();
		
		if (newValueZeroFilter != useZeroFilter){
			priceLoaded = false;
			removePriceChilds();
			useZeroFilter = newValueZeroFilter;
		}
		
		loadPriceNodes();
	}
	
	@Override
	public void open() {
		loadNodes();
	}
	
	private void removePriceChilds(){
		Iterator<TreeNode> iter = getChilds().iterator();
		
		while (iter.hasNext())
			if (iter.next().getClass() == PriceTreeNode.class)
				iter.remove();
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean isLeaf()
	{
		return nodeIsLeaf;
	}
	
	public void setLeaf(boolean val)
	{
		nodeIsLeaf = val;
	}
	
	class PriceData extends DataObject {
		public String name;
		public long rowid;
		public String id;
	}
	
	@Deprecated
	/***
	 * 
	 * @param useZeroFilted
	 */
	public void loadLeafs(boolean useZeroFilted)
	{
//		SQLiteDatabase db = DataBaseManager.getDataBase();
//		String sql = "SELECT rowid, name from price where folderid=?" + (useZeroFilted ? " and qty>0" : "");
//		String[] args = {Integer.toString(id)};
//		Cursor c = db.rawQuery(sql, args);
//		
//		while( c.moveToNext()) {
//			PriceTreeNode node = new PriceTreeNode(this, c.getLong(0), c.getString(1));
//			childs.add(node);			
//		}
//		c.close();
// 1.70
		
		DbReader rdr = new DbReader();
		PriceData pd = new PriceData();
		boolean bdo = rdr.select(pd, DataObjectInfo.getInstance().getTableName(Price.class), getSelection(useZeroFilted));
		while(bdo) {
			PriceTreeNode node = foldersTree.createPriceTreeNode(this, pd.rowid, pd.name, pd.id); //new PriceTreeNode(this, pd.rowid, pd.name);
			childs.add(node);			
			bdo = rdr.selectNext(pd);
		}
		rdr.close();
// 1.70
			
//		List<Long> rids = DbReader.readIds(
//				"price", "folderid=" + Integer.toString(id) + 
//				(useZeroFilted ? " and qty>0" : ""), "name");
//
//		PriceImpl p = new PriceImpl();
//		for( long rid : rids ) {
//			p.read(rid);
//			PriceTreeNode node = new PriceTreeNode(this, p.getRowid(), p.getData().name);
//			childs.add(node);
//		}
//		p.close();
// 3.20
		
		Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
	}

	@Deprecated
	/***
	 * getWhereStr
	 */
	protected String getSelection(boolean useZeroFilted) {
		StringBuilder result = new StringBuilder();
		result.append("folderid=").append(id);
		
		String selection = FoldersTree.getSelection(useZeroFilted);
		
		if (selection.length() > 0)
		{
			result.append(" AND ");
			result.append(selection);
		}
				
		return result.toString();
	}

	protected String getWhereStr(){
		StringBuilder result = new StringBuilder();
		result.append("folderid=").append(id);
		
		String selection = foldersTree.getWhereStr();
		
		if (selection.length() > 0)
		{
			result.append(" AND ");
			result.append(selection);
		}
				
		return result.toString();
	}
	
	@Deprecated
	/***
	 * loadNodes
	 */
	public void loadPriceNodes()
	{
		if (nodeIsLeaf && !priceLoaded)
		{
			if (childs == null) 
				childs = new ArrayList<TreeNode>();
			loadLeafs(useZeroFilter);
			priceLoaded = true;
		}
	}
	
	public void reload() {
		if( priceLoaded ) {
			Iterator<TreeNode> iterator = childs.iterator();
			
			while (iterator.hasNext()) {
				TreeNode node = (TreeNode) iterator.next();
				if (node instanceof PriceTreeNode )
					iterator.remove();
			}
			
			priceLoaded = false;
		}
		loadNodes();
	}
	
	public void loadNodes(){
		if (nodeIsLeaf && !priceLoaded)
		{
			if (childs == null) 
				childs = new ArrayList<TreeNode>();
			
//			Iterator<TreeNode> iterator = childs.iterator();
//			
//			while (iterator.hasNext()) {
//				TreeNode node = (TreeNode) iterator.next();
//				if (!node.isLeaf())
//					iterator.remove();
//			}
			
			List<WarehouseAdapter.PriceInfo> priceInfoList = ((WarehouseAdapter)foldersTree).getPriceInfo(id);
			if (priceInfoList != null)
				for(PriceInfo pi : priceInfoList){
						PriceTreeNode node = foldersTree
								.createPriceTreeNode(this, pi.rowid, pi.name, pi.id); 
						childs.add(node);
					}
			
			Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
			
			priceLoaded = true;
		}
	}
		
	public String getFoldersIds(){
		StringBuilder result = new StringBuilder();
		result.append(Integer.toString(id));
		result.append(",");
		
		for (TreeNode node : childs) {
			if (node instanceof FolderTreeNode)
				result.append(((FolderTreeNode) node).getFoldersIds())
					.append(",");
		}
		
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	@Override
	public int compareTo(TreeNode treeNode) {
		if (this.getClass() != treeNode.getClass())
			return -1;
		else
			return id - ((FolderTreeNode)treeNode).id;
	}

	public FolderTreeNode findFolder(int folderId) {
		if( id == folderId )
			return this;
		
		if(childs != null)
			for( TreeNode tn : childs ) {
				if( tn instanceof FolderTreeNode ) {
					FolderTreeNode ret = ((FolderTreeNode)tn).findFolder(folderId);
					if( ret != null )
						return ret;
				}
			}
		
		return null;
	}
	
	public void setZeroPositionFilter(boolean value){
		useZeroFilter = value;
	}

//	@Override
//	public View getView(Context context, int linesCount) {
//		boolean isOneLine = linesCount == 1;
//		
//		View result = View.inflate(context, getItemLayoutId(), null);
//		TextView tvOrgName = (TextView)result.findViewById(R.id.tvItemSelectRowName);
//		tvOrgName.setLines(linesCount);
//		tvOrgName.setText(name);
//		tvOrgName.setEllipsize(isOneLine ? TruncateAt.END : null);
//		tvOrgName.setHorizontallyScrolling(isOneLine);
//		tvOrgName.setTag(this);
//		
//		return result;
//	}
	
	@Override
	public boolean isFolderNode() {
		return true;
	}
}
