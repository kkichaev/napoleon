package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceRID;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
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
import android.widget.ImageView;
import android.widget.TextView;

public class ItemGroupOrder extends WarehouseNew {
	AgentGroupPlan plan;
	HashMap<String, ItemGroup> groups = new HashMap<String, ItemGroup>();
	List<String> mustBeGroups = new ArrayList<String>();
	boolean loaded = true;
	Set<String> orderItems =new HashSet<String>();
	
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
	
		loadOrderItems();
		
		if(loaded)
			loaded = false;
		else if( adapter instanceof ItemGroupAdapter) {
			((ItemGroupAdapter)adapter).refreshData();
			adapter.notifyDataSetChanged();
		}
	}
	
	private void loadOrderItems() {
		orderItems.clear();
		Date beg = Util.getDate();
		Date end = new Date(beg.getTime() + 1000l * 3600 * 24);
		DatePeriod dp = new DatePeriod(beg, end);
		com.grsoft.napoleon.documents.DocList dl = OrderDoc.instance().docList(document.getId(), null, dp);
		
		for(Document<?> d : dl) {
			OrderImpl oi = (OrderImpl)d;
			for(OrderItem item : oi.getData().items) 
				orderItems.add(item.id);
		}
		
	}

	@Override
	protected int getFolderLayoutId() {
			return R.layout.itemselectrowex;
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
		
		ImageView iv = (ImageView) v.findViewById(R.id.ivQuest);
		
		if (iv != null) {
			iv.setImageResource(R.drawable.no);
			
			if (hasItemInGroup(node.id)) {
				iv.setImageResource(R.drawable.yes);
			}
		}
		
		return v;
	}
	
	private boolean hasItemInGroup(int id) {
		boolean res = false;
		FolderTreeNode r = (FolderTreeNode) adapter.getRootNode();
		
		if (r != null && r.getChildsCount() > id) {
			FolderTreeNode f = (FolderTreeNode) r.getChild(id);
			f.loadNodes();
			
			for (TreeNode n : f.getChilds()) {
				if (n instanceof PriceTreeNode && orderItems.contains(((PriceTreeNode)n).getId())){
					res = true;
					break;
				}
			}
		}
		return res;
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
			int pos = 0;
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
			
			
			Collections.sort(childs, new Comparator<TreeNode>() {
				@Override public int compare(TreeNode arg0, TreeNode arg1) { return arg0.toString().compareTo(arg1.toString()); }
			});
//			Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
			priceLoaded = true;
		}
	}
}
