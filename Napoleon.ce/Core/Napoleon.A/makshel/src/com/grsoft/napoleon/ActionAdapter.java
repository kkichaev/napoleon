package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import android.content.Context;
import android.os.AsyncTask;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.ActionItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Gift;
import com.grsoft.dataobjects.GiftItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.GiftImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;


public class ActionAdapter extends FoldersAdapter {
	private int idx = 0;
	private PriceImpl price = new PriceImpl();
	
	public ActionAdapter(WarehouseManager warehouse) { 
		super(warehouse); 
	}
	
	@Override
	protected String getFolderTableName() {	return DataObjectInfo.getInstance().getTableName(Action.class);	}
	
	@Override
	public void buldProcess(AsyncTask<?, ?, ?> task) {
		if (!task.isCancelled()){
			DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>(){

				@Override
				public boolean travel(DataTraveler<Action> item) {
					ActionNode node = new ActionNode(ActionAdapter.this, folderTop);
					node.id = idx;
					node.level = 0;
					node.name = item.data.name;
					node.descr = item.data.descr;
					node.actionid  = item.data.id;
					node.setLeaf(true);
					
					for(ActionItem ai : item.data.items)
						if (price.read("id", ai.id))
							node.addNode(price);
					
					root.insert(node);
					idx++;
					
					return true;
				}}, null);
			
			for(Entry<String, String> en : ((WarehouseEx)warehouse).giftitem.entrySet()){
				GiftNode n = new GiftNode(ActionAdapter.this, folderTop, en.getKey());
				n.id = idx++;
				
				StringBuilder sb = new StringBuilder();
				GiftImpl gift = new GiftImpl();
				if(gift.read("giftid", en.getValue()))
				{
					Gift g = gift.getData();
					
					for(GiftItem i : g.items){
						PriceImpl p = new PriceImpl();
						p.read("id", i.id);
						
						if(sb.length() > 0)
							sb.append(" или ");
						
						sb.append(p.getData().name);
					}
					
					PriceImpl p = new PriceImpl();
					p.read("id", en.getKey());
					n.name = ((Context)warehouse).getString(R.string.gift_purpose, p.getData().name, Util.IntToScaleStr(g.qty, Consts.QTY_SCALE), sb.toString());
					root.insert(n);
				}
			}
			
			warehouse.afterBuildSet();
		}
	}
}

class ActionNode extends FolderTreeNode{
	protected String actionid = "";
	public String descr = ""; 
	public ActionNode(TreeNodeFactory foldersTree, FolderTreeNode parent) {
		super(foldersTree, parent);
		priceLoaded = true;
	}
	
	public void addNode(PriceImpl price){
		if (childs == null) 
			childs = new ArrayList<TreeNode>();
		
		
		Price p = price.getData();
		PriceTreeNode node = foldersTree.createPriceTreeNode(this, price.getRowid(), p.name, p.id); 
		childs.add(node);
	}
	
	public void sortChilds(){
		if(childs != null)
			Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
	}
}

class GiftNode extends FolderTreeNode{

	public GiftNode(TreeNodeFactory foldersTree, FolderTreeNode parent, String id) {
		super(foldersTree, parent);
		priceLoaded = true;
		
		if (childs == null) 
			childs = new ArrayList<TreeNode>();
		
		PriceImpl pp = new PriceImpl();
		pp.read("id", id);
		
		Price p = pp.getData();
		PriceTreeNode node = foldersTree.createPriceTreeNode(this, pp.getRowid(), p.name, p.id); 
		childs.add(node);
	}
	
}


