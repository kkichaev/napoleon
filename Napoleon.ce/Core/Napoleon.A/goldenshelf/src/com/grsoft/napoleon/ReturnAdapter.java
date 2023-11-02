package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import android.database.sqlite.SQLiteDatabase;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.ContractDefItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.WarehouseManager;


public class ReturnAdapter extends FoldersAdapter {
	List<ContractDef> data = new ArrayList<ContractDef>();
	
	public ReturnAdapter(WarehouseManager warehouse) {
		super(warehouse);
		
		final long time = new Date().getTime();
		
		
		DataTraveler.travel(ContractDef.class, new DataTraveler.Travel<ContractDef>() {
			@Override
			public boolean travel(DataTraveler<ContractDef> item) {
				data.add(item.data);
				item.data = new ContractDef();
				return true;
			}}, String.format("start<=%d and finish>=%d", time, time));
		
	}

	
	@Override
	protected void fillTree(SQLiteDatabase database) {
		root.getChilds().clear();
		
		for(ContractDef c : data){
			ReturnNode node = (ReturnNode) createFoldersTreeNode(root);
			node.id = 0;
			node.level = 0;
			node.name = c.name;
			node.setLeaf(true);
			node.contract = c;
			root.insert(node);
		}
	}
	
	@Override
	protected FolderTreeNode createFoldersTreeNode(FolderTreeNode parent) {
		return new ReturnNode(this, parent);
	}
}

class ReturnNode extends FolderTreeNode{
	public ContractDef contract;
	
	public ReturnNode(TreeNodeFactory foldersTree, FolderTreeNode parent) {
		super(foldersTree, parent);
	}
	
	@Override
	public void open() {
		
		
		if (childs == null){ 
			childs = new ArrayList<TreeNode>();
		
			PriceImpl pimpl = new PriceImpl();
			PriceEx pi = (PriceEx) pimpl.getData();
			for(ContractDefItem i : contract.items){
				
				if (pimpl.read("id", i.id) && pi.my == 1){
					PriceTreeNode node = foldersTree.createPriceTreeNode(this, pimpl.getRowid(), pi.name, pi.id); 
					childs.add(node);
				}
			}
		
			Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
			
			priceLoaded = true;
			pimpl.close();
		}
	}
	
}
