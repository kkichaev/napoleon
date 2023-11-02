package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;
import com.grsoft.util.WarehouseManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class FoldersAdapterEx extends FoldersAdapter {

	public FoldersAdapterEx(WarehouseManager warehouse) {
		super(warehouse);
		
		TreeNodeComparator = new TreeNodeCmp() {
			PriceImpl p1 = new PriceImpl();
			PriceImpl p2 = new PriceImpl();
			
			@Override
			public int compare(TreeNode lhs, TreeNode rhs) {
				int res = 0;
				
				if (lhs instanceof PriceTreeNode && rhs instanceof PriceTreeNode) {
					p1.read(((PriceTreeNode)lhs).getRowid(), false);
					p1.close();
					p2.read(((PriceTreeNode)rhs).getRowid(), false);
					p2.close();
					
					res = ((PriceEx)p1.getData()).pos - ((PriceEx)p2.getData()).pos; 
				}
				
				if (res == 0)
					res = super.compare(lhs,rhs);
				
				return res;
			}
		};
	}

	protected void fillTree(SQLiteDatabase database) {
		try{
			root.getChilds().clear();
			makeTree(null,root);
			moveChilds(root);
			deleteEmptyNodes(root);
			sortFullTree(root);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void makeTree(Cursor cursor, FolderTreeNode parent)
	{
		FolderTreeNode curParent = parent;
		String[] foldersItem = ((Context)warehouse).getResources().getStringArray(R.array.folders_item);
		
		for(int i = 0; i < foldersItem.length; i++)
		{
			FolderTreeNode node = createFoldersTreeNode(parent);
			node.id = i;
			node.level = 0;
			node.name = foldersItem[i];
			node.setLeaf(true);
			curParent.insert(node);
		}
	}
	
	protected void fillPriceIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
			
			if(DbWriter.isTableExists(priceTable)){
				SQLiteQueryBuilder fPriceQuery = new SQLiteQueryBuilder();
				fPriceQuery.setDistinct(true);
				fPriceQuery.setTables(priceTable);
				
				Cursor cursor = fPriceQuery.query(database, new String[] {"folderid", 
						"rowid", "name", "id", "own"}, getWhereStr(), null, null, null, null);
				
				if (cursor.moveToFirst()) {
					try{
						do{
							long rowid = cursor.getLong(1);
							String id = cursor.getString(3);
							int folderid = cursor.getInt(4);
							
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
}
