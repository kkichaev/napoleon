package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.AgentGroupPlan;
import com.grsoft.dataobjects.AgentGroupPlanItem;
import com.grsoft.dataobjects.AgentNeedSell;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.ItemGroup;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.PriceRID;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseAdapter;
import com.grsoft.util.WarehouseManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemGroupOrder extends WarehouseNew {
	AgentGroupPlan plan;
	HashMap<String, ItemGroup> groups = new HashMap<String, ItemGroup>();
	List<String> mustBeGroups = new ArrayList<String>();
	boolean loaded = true;
	
	public static void open(Context context, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(context, ItemGroupOrder.class);
		
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, true);

		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		plan = AgentGroupPlan.getPlan();
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(loaded)
			loaded = false;
		else if( adapter instanceof ItemGroupAdapter) {
			((ItemGroupAdapter)adapter).refreshData();
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override protected int getLayoutId() { return R.layout.item_group_layout; }
	@Override protected BaseAdapter createListAdapter() { return new ItemGroupAdapter(this); }
	
	@Override
	public View getFolderView(FolderTreeNode node, View convertView) {
		View v = super.getFolderView(node, convertView); 
		
		TextView tv;
		tv = (TextView)v.findViewById(R.id.tvSales);
		FolderTreeNodeEx fe = (FolderTreeNodeEx)node;		
		tv.setText(Util.IntToScaleStr(fe.weight, Consts.WEIGHT_SCALE));

		int color = (mustBeGroups.contains(fe.group.id)) ? Color.RED : Color.BLACK;
		tv = (TextView)v.findViewById(R.id.tvItemSelectRowName);;
		tv.setTextColor(color);
		
		return v;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void updateTotalSum() {
		super.updateTotalSum();
		if(document != null) {
			AgentNeedSell ans = plan.check((OrderEx)document.getData());
			String info = String.format("Необходимо заказать %d НГ, %s кг", ans.needSell, 
					Util.IntToScaleStr(ans.weight, Consts.WEIGHT_SCALE));
			TextView tv = (TextView)findViewById(R.id.tvInfo);
			tv.setText(info);
		}
	}
	
	class ItemGroupAdapter extends FoldersAdapter {

		public ItemGroupAdapter(WarehouseManager warehouse) {
			super(warehouse);
		}
		
		HashMap<String, Integer> loadItemGroups() {
			OrderEx oe = (OrderEx)document.getData();
			HashMap<String, Integer> wi = oe.weightByGroup();
			AgentNeedSell ans = plan.check(oe);
		
			mustBeGroups.clear();
			HashMap<String, Integer> ret = new HashMap<String, Integer>();
			for(AgentGroupPlanItem ai : ans.items) {
				ret.put(ai.id, ai.weight);
				wi.remove(ai.id);
				mustBeGroups.add(ai.id);
			}
			
			for(ItemGroup ig : groups.values()) {
				String key = ig.id;
				if(ret.containsKey(key))
					continue;
				Integer val = wi.get(key);
				if(val == null)
					val = 0;
				if( val >= plan.weight)
					continue;
				ret.put(key, plan.weight - val);
			}
			
			return ret;
		}
		
		@Override
		public int getNextFolderID(boolean next, int id) {
			int retId = -1;
			for(TreeNode tn : root.getChilds()) {
				if(tn instanceof FolderTreeNode) {
					FolderTreeNode ftn = (FolderTreeNode)tn;
					if(ftn.id < id && !next && retId < ftn.id)
						retId = ftn.id;
					else if(ftn.id > id && next && (retId > ftn.id || retId == -1) )
						retId = ftn.id;
				}
			}
			return retId;
		}
		
		public void refreshData() {
			if(groups.size() == 0)
				loadGroups();
			
			ArrayList<TreeNode> list = root.getChilds(); 
			if(list != null)
				list.clear();
			
			HashMap<String, Integer> needGroups = loadItemGroups();
			for(Entry<String,Integer> kv : needGroups.entrySet()) {
				if(groups.containsKey(kv.getKey()) == false)
					continue;
				
				FolderTreeNode f = new FolderTreeNodeEx(this, root, groups.get(kv.getKey()), kv.getValue());
				root.insert(f);
			}
			
			Collections.sort(root.getChilds(), FoldersAdapter.TreeNodeComparator);
			int pos = 1;
			for(TreeNode tn : root.getChilds())
				((FolderTreeNode)tn).id = pos++;
		}
		
		@Override
		public synchronized void buldProcess(AsyncTask<?, ?, ?> task) {
			if(task.isCancelled() || document == null)
				return;
			refreshData();
			warehouse.afterBuildSet();
		}

		private void loadGroups() {
			groups.clear();
			DataTraveler.travel(ItemGroup.class, new DataTraveler.Travel<ItemGroup>(true) {

				@Override
				public boolean travel(DataTraveler<ItemGroup> item) {
					groups.put(item.data.id, item.data);
					return true;
				}
			}, "");
		}		
	}
}

class FolderTreeNodeEx extends FolderTreeNode {
	public ItemGroup group;
	public int weight;

	public FolderTreeNodeEx(TreeNodeFactory foldersTree, FolderTreeNode parent, ItemGroup group, int weight) {
		super(foldersTree, parent);

		this.group = group;
		this.weight = weight;
		
		name = group.name;
		level = 0;
		nodeIsLeaf = true;
	}
	
	@Override
	public int compareTo(TreeNode treeNode) {
		if (this.getClass() != treeNode.getClass())
			return -1;
		return group.name.compareTo(((FolderTreeNodeEx)treeNode).group.name);
	}
	
	
//	@Override
//	public int compareTo(TreeNode treeNode) {
//		if(treeNode instanceof FolderTreeNodeEx)
//			return group.name.compareTo(((FolderTreeNodeEx)treeNode).group.name);
//		return super.compareTo(treeNode);
//	}
	
	@Override
	public void loadNodes() {
		if (nodeIsLeaf && !priceLoaded)
		{
			if (childs == null) 
				childs = new ArrayList<TreeNode>();
			else
				childs.clear();
			
			String where = "itemGroup='" + group.id + "'";
			String filters = foldersTree.getWhereStr();
			if(filters.length() > 0) {
				where += " and " + filters;
			}
			
			DataTraveler.travel(PriceRID.class, new DataTraveler.Travel<PriceRID>() {
	
				@Override
				public boolean travel(DataTraveler<PriceRID> item) {
					PriceTreeNode node = foldersTree.createPriceTreeNode(FolderTreeNodeEx.this, 
							item.data.rowid, item.data.name, item.data.id); 
					if(((WarehouseAdapter)foldersTree).inset(item.data.rowid, item.data.id, id))
						childs.add(node);
					return true;
				}
			}, where);
			
			
			Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
			priceLoaded = true;
		}
	}
}
