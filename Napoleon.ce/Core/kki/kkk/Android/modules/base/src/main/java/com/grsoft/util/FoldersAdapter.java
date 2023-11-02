package com.grsoft.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.FilterAdapter;

public class FoldersAdapter extends WarehouseAdapter 
	implements FilterAdapter {
	protected static FolderTreeNode globalRoot;
	protected static Map<Integer, ArrayList<PriceInfo>> globalPrice;
	static String filterData = "";
	
	public static Comparator<TreeNode> TreeNodeComparator = new TreeNodeCmp(); 
	
	public FoldersAdapter(WarehouseManager warehouse) {
		super(warehouse);
	}
	
	public static String FOLDERS_ADAPTER = "FolderAdapter";
	
	public String getName() { return FOLDERS_ADAPTER; }

	protected void fillPriceIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			String priceTable = getPriceTableName();
			
			if(DbWriter.isTableExists(priceTable)){
				SQLiteQueryBuilder fPriceQuery = new SQLiteQueryBuilder();
				fPriceQuery.setDistinct(true);
				fPriceQuery.setTables(priceTable);
				
				Cursor cursor = fPriceQuery.query(database, new String[] {"folderid", 
						"rowid", "name", "id"}, getWhereStr(), null, null, null, null);
				
				if (cursor.moveToFirst()) {
					try{
						do{
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);
							int folderid = cursor.getInt(0);
							
							if( !inset( rowid, id, folderid ) )
								continue;
														
							if(!fprice.containsKey(folderid))
								fprice.put(folderid, new ArrayList<PriceInfo>());
							
							PriceInfo pi = new PriceInfo(rowid, cursor.getString(2), id);
							fprice.get(folderid).add(pi);
						} while(cursor.moveToNext());
					} finally { 
						cursor.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected String getPriceTableName() { return DataObjectInfo.getInstance().getTableName(Price.class); }
	protected String getFolderTableName() { return DataObjectInfo.getInstance().getTableName(Folder.class); }
	
	protected void fillTree(SQLiteDatabase database) {
		try{
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String folderTable = getFolderTableName();
			
			queryBuilder.setTables(folderTable);
			Cursor folders = queryBuilder.query(database, 
					new String[] {"name", "level", "id"}, null, null, null, null, "id");
			
			try{
				if (folders.moveToFirst()){
					root.getChilds().clear();
					makeTree(folders,root);
					moveChilds(root);
					deleteEmptyNodes(root);
					sortFullTree(root);
				}
				
			} finally {
				folders.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void moveChilds(FolderTreeNode parent){
		int level = ((CfgNplW)ConfigManager.getConfig()).priceLevel;
		
		if (level > 0){
			for(TreeNode n: parent.getChilds())
				if(n instanceof FolderTreeNode){
					FolderTreeNode fn = (FolderTreeNode)n;
					
					for(TreeNode nn : fn.getChilds())
						if(nn instanceof FolderTreeNode)
							moveChildToParent(fn, (FolderTreeNode)nn, level);
				}

		}
	}
	
	protected void moveChildToParent(FolderTreeNode parent, FolderTreeNode child, int level) {
		for(TreeNode n : child.getChilds())
			if(n instanceof FolderTreeNode){
				FolderTreeNode ftn = (FolderTreeNode)n;
				if (ftn.level > level)
					moveChildToParent((FolderTreeNode)ftn.getParent(), (FolderTreeNode)n, level);
			}

		if(child.level > level && fprice.containsKey(child.id)){
			if(!fprice.containsKey(parent.id)) 
				fprice.put(parent.id, new ArrayList<PriceInfo>());
			
			fprice.get(parent.id).addAll(fprice.get(child.id));
			fprice.remove(child.id);
			
			child.setLeaf(false);
			
			if(fprice.get(parent.id).size() > 0)
				parent.setLeaf(true);
		}
	}

	
	protected void makeTree(Cursor cursor, FolderTreeNode parent)
	{
		FolderTreeNode curNode = parent;
		FolderTreeNode curParent = parent;
		
		do
		{
			FolderTreeNode node = createFoldersTreeNode(parent);
			node.id = cursor.getInt(2);
			node.level = cursor.getInt(1);
			node.name = cursor.getString(0);
			node.setLeaf(fprice.containsKey(node.id));
			
			if(node.level == curNode.level)
			{
				curParent.insert(node);
			}
			else if (node.level > curNode.level)
			{
				curNode.insert(node);
				curParent = curNode;
			}
			else
			{
				while(node.level < curNode.level && curNode != parent)
					curNode = (FolderTreeNode) curNode.getParent();
				
				if( node.level > curNode.level )
					curParent = curNode;
				else
					curParent = (curNode == parent) ? curNode : (FolderTreeNode) curNode.getParent();
				curParent.insert(node);
			}
			
			curNode = node;
		}
		while(cursor.moveToNext());
	}
		
	protected void sortFullTree(TreeNode node){
		Collections.sort(node.getChilds(), TreeNodeComparator);

		for (TreeNode child: node.getChilds())
			if (child.getChilds().size() > 0)
				sortFullTree(child);
	}
	
	protected void deleteEmptyNodes(FolderTreeNode node) {
		FolderTreeNode begin = node;
		boolean cleared = false;
		
		do{
			node = begin;
			cleared = false;
			
			for(Iterator<TreeNode> iter = node.getChilds().iterator(); 
					iter.hasNext();){
				TreeNode nn = iter.next();
				
				if(nn instanceof FolderTreeNode){
					FolderTreeNode testNode = (FolderTreeNode)nn;
					
					if (testNode.getChildsCount() > 0)
						deleteEmptyNodes(testNode);
					
					if (testNode.getChildsCount() == 0 && 
						!testNode.isLeaf()){
							iter.remove();
							cleared = true;
					}
				}
			}
		}while(cleared);
	}

	
	
	@Override
	public void upLevel() {
		if(!isTop()){
			TreeNode parent = folderTop.getParent();
			int pos = 0;
			
			if (folderTop != null && parent != null)
				pos = parent.indexOf(folderTop);
			
			folderTop = (FolderTreeNode) (parent == null ? root : parent);
			folderTop.reload();
			
			fireDataSetChanged();
			fireSetSelection(pos);
		}
		
	}
	
//	private void setNextFolder(String sql){
//		Cursor c = null;
//		try{
//			c = DataBaseManager.getDataBase().rawQuery(sql, null);
//			if(c.moveToFirst())
//				setFolder(c.getInt(0));
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally{
//			if(c != null)
//				c.close();
//		}
//	}

	public int getNextFolderID(boolean next, int id) {
		if( fprice.size() == 0 || solidPrice )
			return - 1;
		int res = folderTop.id;
		for(Integer i : fprice.keySet()) {
			if(next) {
				if( i > id ) {
					if( res == id )
						res = i;
					else if( i < res )
						res = i;
				}
			} else {
				if( i < id ) {
					if( res == id )
						res = i;
					else if( i > res )
						res = i;
				}				
			}
		}
		return res;
	}
	
	@Override
	public void nextFolder() {
		int id = folderTop.id;
		int res = getNextFolderID(true, id);
		if( res >= 0 && res != id )
			setFolder(res);
//		StringBuilder sql = new StringBuilder("select min(folderID) from price");
//		
//		if(folderTop.id > 0)
//			sql.append(" where folderID >").append(folderTop.id);
//		
//		setNextFolder(sql.toString());
	}
	
	@Override
	public void prevFolder() {
		int id = folderTop.id;
		int res = getNextFolderID(false, id);
		if( res >= 0 && res != id )
			setFolder(res);

//		StringBuilder sql = new StringBuilder("select max(folderID) from price");
//		
//		if(folderTop.id > 0)
//			sql.append(" where folderID <").append(folderTop.id);
//		
//		setNextFolder(sql.toString());		
	}
	

	@Override
	public void applyFilter(String value) {
		warehouse.applySearchFilter(value);
	}

	@Override
	public void resetFilter() {
		PriceTextFilter filter = (PriceTextFilter) getFilter(PriceTextFilter.NAME);
		
		if (filter != null){
			filters.remove(filter);
			buildSet(warehouse.isPriceExpand());
		}
	}

	public static void resetCache() {
		globalPrice = null;
		globalRoot = null;
		filterData = "";
	}
	
	public String filterNames() {
		String f = getName();
		for(Filter flt : filters) {
			f += flt.getName();
			String where = flt.getWhereStr();
			if(where != null)
				f += where;
		}
		
		return f;
	}
	
	@Override
	public synchronized void buldProcess(AsyncTask<?, ?, ?> task) {
		String fd = filterNames(); 
		if( fd.compareTo(filterData) != 0 ) {
			resetCache();
			filterData = fd;
		}
		
		SQLiteDatabase database = DataBaseManager.getDataBase();
		
		if (!task.isCancelled()){
			if(!solidPrice) {
				if( globalPrice == null ) {
					fillPriceIds(database);
					globalPrice = fprice;
				} else
					fprice = globalPrice;
				
				if( globalRoot == null ) {
					fillTree(database);
					globalRoot = root;
				} else
					root = globalRoot;
			} else {
				fillPriceIds(database);
				
				if(folderTop.getChilds().size() == 0)
					fillTree(database);
				
				priceTop = createFoldersTreeNode(null);
				Collection<ArrayList<PriceInfo>> piListList = fprice.values();
				
				for(ArrayList<PriceInfo> val : piListList)
					for(PriceInfo pi : val) {
						priceTop.getChilds().add(createPriceTreeNode(priceTop, pi.rowid, pi.name, pi.id));
					}
				
				warehouse.sortingPriceList(priceTop.getChilds());				
			}
			
			warehouse.afterBuildSet();
		}
		
	}

	@Override
	public boolean isValid(TreeNode node) {
		return true;
	}
}