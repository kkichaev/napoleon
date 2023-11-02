/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Дерево Folders
 *
 * kki   25/11/2010   creating
 */

package com.grsoft.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.SQLSelector;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;

/***
 * Дерево папок Folders для структуры прайса
 * Содержит узлы  FolderTreeNode, структура
 * которых создается из таблицы folder.
 * 
 * Узел FolderTreeNode, может содержать 
 * другие узлы FolderTreeNode, которые представляют собой 
 * "папки прайса" или PriceTreeNode - товар, при
 * построение дерева PriceTreeNode не добавляются, они
 * будут загружены при выборе конкретного узла FolderTreeNode
 * 
 * @author kki
 *
 */
public class FoldersTree implements TreeNodeFactory
{
	public static Map<Class<? extends DocType>, SQLSelector> docSelection 
		= new HashMap<Class<? extends DocType>, SQLSelector>();
	
	private FolderTreeNode root;
	private ArrayList<Integer> fprice = new ArrayList<Integer>();
	public TreeNode top;
	
//	private final int DIR_UP = 1;
//	private final int DIR_DOWN = 2;
	
	private Itemsable document;
	
	public FoldersTree(Itemsable document){
		this(false, document);
	}
	
	public FoldersTree(boolean useZeroFilter, Itemsable document){
		this.root = new FolderTreeNode(this, null);
		this.top = root;
		this.document = document;
		
		SQLiteDatabase database = DataBaseManager.getDataBase();

		fillPriceIds(database, useZeroFilter);
		fillTree(database);
	}

	public FoldersTree(String matrixName, boolean zeroFilter,
			Itemsable document){
		this(matrixName, "", zeroFilter, document);
	}
	
	public FoldersTree(String matrixName, String filter, boolean zeroFilter, 
			Itemsable document){
		this.root = new FolderTreeNode(this, null);
		this.document = document;
		this.top = root;
		
		MatrixImpl matrix = new MatrixImpl();
		matrix.getData().name = matrixName;
		
		if (matrix.read())
			addItems(matrix.getData().items, filter, zeroFilter);
		
		matrix.close();
	}
	
	protected FoldersTree(MatrixItem item, Itemsable document) {
		this.root = new FolderTreeNode(this, null);
		this.document = document;
	}
	
	protected void addItems(List<MatrixItem> items, String filter, boolean zeroFilter) {
		ArrayList<TreeNode> childs = root.getChilds();
		PriceImpl p = new PriceImpl();
		Price prc = p.getData();
		
		String table = DataObjectInfo.getInstance().getTableName(prc.getClass());
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String[] keys = { "" };
		String sql = "SELECT rowid, name, id FROM " + table + " WHERE id=?";
		
		String selection = getSelection(zeroFilter);
		
		if(selection.length() > 0)
			sql += " AND ";
		
		sql += selection;
		
		try {
			String fltUpper = filter.toUpperCase(Locale.getDefault());
			SQLiteCursor c = (SQLiteCursor) db.rawQuery(sql, keys);
			for(MatrixItem mi : items){
				keys[0] = mi.id;
				c.setSelectionArguments(keys);
				c.requery();
				if( c.moveToNext() ) {
					String name = c.getString(1);
					if( name.toUpperCase(Locale.getDefault()).contains(fltUpper) ) {
						PriceTreeNode node = createPriceTreeNode(root, c.getLong(0), name, c.getString(2));
						childs.add(node);
					}
				}
			}
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		top = root;
	}
	
	private void fillTree(SQLiteDatabase database) {
		try{
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String folderTable = DataObjectInfo.getInstance().getTableName(Folder.class);
			
			queryBuilder.setTables(folderTable);
			Cursor folders = queryBuilder.query(database, 
					new String[] {"name", "level", "id"}, 
					null, null, null, null, null);
			
			try{
				if (folders.moveToFirst()){
					makeTree(folders,root);
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

	private void fillPriceIds(SQLiteDatabase database, boolean useZeroFilter) {
		try{
			String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
			if(DbWriter.isTableExists(priceTable)){
				SQLiteQueryBuilder fPriceQuery = new SQLiteQueryBuilder();
				fPriceQuery.setDistinct(true);
				fPriceQuery.setTables(priceTable);
				
				Cursor folderID = fPriceQuery.query(database, new String[] {"folderid"}, 
						getSelection(useZeroFilter), null, null, null, null);
				
				if (folderID.moveToFirst()) {
					try{
						do
							fprice.add(folderID.getInt(0));
						while(folderID.moveToNext());
					} finally { 
						folderID.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public interface QtyZeroFilter {
		public String getFilter();
	}
	public static QtyZeroFilter ZeroFilterStr = new QtyZeroFilter() {
		@Override public String getFilter() { return "qty>0"; }
	};
	
	public static String getSelection(boolean useZeroFilter){
		StringBuilder result = new StringBuilder(useZeroFilter ? ZeroFilterStr.getFilter() : "");
		Class<?> curDocClass = DocType.getCurDoc().getClass();
		if (docSelection.containsKey(curDocClass))
		{
			if (result.length() > 0)
				result.append(" AND ");
			
			result.append(docSelection.get(curDocClass).getWhereClause());
		}
		
		return result.toString();
	}
	
	@Override
	public String getWhereStr(){
		return "";
	}

	private void deleteEmptyNodes(FolderTreeNode node) {
		FolderTreeNode begin = node;
		boolean cleared = false;
		
		do{
			node = begin;
			cleared = false;
			
			for(Iterator<TreeNode> iter = node.getChilds().iterator(); 
					iter.hasNext();){
				FolderTreeNode testNode = (FolderTreeNode)iter.next();
				
				if (testNode.getChildsCount() == 0 && 
					!testNode.isLeaf()){
						iter.remove();
						cleared = true;
					}
				
				if (testNode.getChildsCount() > 0)
					deleteEmptyNodes(testNode);
			}
		}while(cleared);
	}

	private void sortFullTree(TreeNode node)
	{
		Collections.sort(node.getChilds(), FoldersAdapter.TreeNodeComparator);

		for (TreeNode child: node.getChilds())
		{
			if (child.getChilds().size() > 0)
				sortFullTree(child);
		}
	}
	
	private void makeTree(Cursor cursor, FolderTreeNode parent)
	{
		FolderTreeNode curNode = parent;
		FolderTreeNode curParent = parent;
		
		do
		{
			FolderTreeNode node = createFoldersTreeNode(parent);
			node.id = cursor.getInt(2);
			node.level = cursor.getInt(1);
			node.name = cursor.getString(0);
			node.setLeaf(fprice.contains(node.id));
			
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

	protected FolderTreeNode createFoldersTreeNode(FolderTreeNode parent) {
		return new FolderTreeNode(this, parent);
	}
	
	@Override
	public PriceTreeNode createPriceTreeNode(TreeNode parent, long priceRowId, String name, String id){
		return new PriceTreeNode(root, priceRowId, name, document);
	}

	public void setTop(TreeNode node)
	{
		top = node;
	}
	
	public TreeNode getTop(){
		return top;
	}
	
	public boolean isTop()
	{
		return top == root;
	}
	
//	public boolean nextLeaf()
//	{
//		TreeNode oldTop = top;
//		top = getNextLeaf(top, DIR_DOWN);
////		top = getNextLeaf2(top, DIR_DOWN);
//		return (oldTop != top);
//	}
	
//	public TreeNode getNextLeaf(TreeNode node, int dir )
//	{
//		int index = 0;
//		
////		if (node.getClass() != FolderTreeNode.class)
////		{
////			TreeNode grandPa = node.getParent().getParent();
////			index = grandPa.indexOf(node.getParent());
////			index = dir == DIR_DOWN ? index++ : index--;
////			TreeNode nextNode = grandPa.get(index);
////			
////			return nextNode;
////		}
//		
//		ArrayList<TreeNode> childs = null;
//		
//		if (node != root)
//		{
//			
//			
////			if (node.hasChilds())
////			{
////				index = dir == DIR_DOWN ? 0 : node.getChildsCount() - 1;
////				getNextLeaf(node.getChild(index) , dir);
////			}
//			
//			TreeNode parent = node.getParent(); 
//			index = parent.getChilds().indexOf(node);
//			int size = parent.getChilds().size();
//			
//			switch(dir)
//			{
//				case DIR_DOWN:
//					if ( ++index >= size)
//						return getNextLeaf(parent, dir);
//					break;
//				case DIR_UP:
//					if ( --index < 0)
//						return getNextLeaf(parent, dir);
//					break;
//			}
//			
//			childs = parent.getChilds();
//		}
//		else
//		{
//			childs = node.getChilds();
//			int size = childs.size(); 
//			if ( size > 0)
//				 index = dir == DIR_DOWN ? 0 :  size - 1;
//			else
//				return node;
//		}
//			
//		TreeNode nextNode = childs.get(index);
//		
//		if (nextNode instanceof PriceTreeNode)
//			return getNextLeaf(nextNode.getParent(), dir);
//		
//		if(nextNode.isLeaf())
//		{
//			if (nextNode instanceof FolderTreeNode)
//				((FolderTreeNode)nextNode).loadPriceNodes();
//			return nextNode;
//		}
//		else
//		{
//			if (nextNode.getChilds().size() == 0)
//				return getNextLeaf(nextNode, dir);
//			
//			index = dir == DIR_DOWN ? 0 : nextNode.getChilds().size() - 1;
//			FolderTreeNode tryNode = (FolderTreeNode)nextNode.getChilds().get(index);
//			
//			if (tryNode.isLeaf())
//			{
//				tryNode.loadPriceNodes();
//				return tryNode;
//			}
//			else
//				return getNextLeaf(tryNode, dir);
//		}
//	}
//	
//	private TreeNode getNextLeaf2(TreeNode parent, int dir)
//	{
//		if (parent != root)
//		{
//			for(TreeNode n : parent.getChilds())
//			{
//				if (n.isLeaf())
//				{
//					((FolderTreeNode)n).loadPriceNodes();
//					return n;
//				}
//				else if (n.hasChilds())
//					return getNextLeaf(n, dir);
//			}
//		}
//		
//		return getNextLeaf(parent, dir);
//	}
	
//	public boolean prevLeaf()
//	{
//		TreeNode oldTop = top;
//		top = getNextLeaf(top, DIR_UP);
//		return (top != oldTop);
//	}

	public String getFoldersIds() {
		if(!isTop() && 
		   top instanceof FolderTreeNode)
		{
			return ((FolderTreeNode)top).getFoldersIds();
		}
		else
			return "";
	}

	public boolean findFolder(int folderId) {
		FolderTreeNode n = root.findFolder(folderId);
		if( n != null ) {
			top = n;
			return true;
		}
		return false;
	}
	
	public Itemsable getDocument(){
		return document;
	}

	@Override
	public boolean isValid(TreeNode node) {
		return true;
	}
}
