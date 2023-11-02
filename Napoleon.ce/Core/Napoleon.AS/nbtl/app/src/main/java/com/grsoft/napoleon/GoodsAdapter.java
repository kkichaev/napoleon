package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.GroupGoods;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.GoodsMatrixImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.MatrixOrderComparer;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.WarehouseManager;
import android.database.sqlite.SQLiteDatabase;


public class GoodsAdapter extends FoldersAdapter {
	protected HashSet<String> priceFilter = new HashSet<String>();
	protected Document<?> document;
	boolean onlyMy;
	
	public GoodsAdapter(WarehouseManager warehouse, Document<?> doc, boolean onlyMy) {
		super(warehouse);
		this.document = doc;
		this.onlyMy = onlyMy;
		if(onlyMy)
			fillFilterArray(doc.getId());
	}

	protected void fillFilterArray(String orgid) {
		OrgImpl org = new OrgImpl();
		org.read("id", orgid);
		
		String mname = ((OrgEx)org.getData()).goodsMatrix;
		if(mname.trim().length() > 0){
			GoodsMatrixImpl gimpl = new GoodsMatrixImpl();
			gimpl.read("name", mname);
			
			Matrix mtx = new Matrix();
			mtx.name = mname;
			
			for(MatrixItem m : gimpl.getData().items) {
				mtx.items.add(m);
				priceFilter.add(m.id);
			}
			if(mtx.items.size() > 0)
				FoldersAdapter.TreeNodeComparator = new MatrixOrderComparer(mtx);
		}
	}

	@Override
	public String getWhereStr() {
		String ret = super.getWhereStr();
		if(ret.length() > 0)
			ret += " AND ";
		ret += "isGoods=1";
		if( onlyMy )
			ret += " AND my=1";
		return ret;
	}
	
	@Override
	protected void fillTree(SQLiteDatabase database) {
		root.getChilds().clear();
		
		final Map<String, FolderTreeNode> data = new HashMap<String, FolderTreeNode>();
		final Set<String> processed = new HashSet<String>();
		final List<FolderTreeNodeEx> list = new ArrayList<FolderTreeNodeEx>();
		final PriceImpl price = new PriceImpl();
		
		DataTraveler.travel(GroupGoods.class, new DataTraveler.Travel<GroupGoods>() {
			@Override
			public boolean travel(DataTraveler<GroupGoods> item) {
				FolderTreeNodeEx node = (FolderTreeNodeEx) createFoldersTreeNode(root);
				node.fid = item.data.id;
				node.pid = item.data.fid;
				node.name = item.data.name;
				
				String where = "[fid]=\""+item.data.id+"\"";
				if(onlyMy)
					where += " AND my=1";
				List<Long> ids = DbReader.readIds(price.getTableName(), where, null);
	           	  
	           	for(long rowid : ids)
	           		if(price.read(rowid)){
	           			if(priceFilter.size() == 0 || priceFilter.contains(price.getData().id)){
		           			PriceTreeNode ptn = createPriceTreeNode(node, rowid, price.getData().name, price.getData().id);
		     				node.insert(ptn);
	           			}
	           		}
				
				//Collections.sort(childs, FoldersAdapter.TreeNodeComparator);
				
				if(item.data.fid.trim().length() == 0)
					root.insert(node);
				
				if(data.containsKey(item.data.fid)){
					data.get(item.data.fid).insert(node);
					processed.add(item.data.id);
				}
				
				if(!data.containsKey(item.data.id))
					data.put(item.data.id, node);
					
				list.add(node);
				
				return true;
			}}, null);
		
		price.close();
		
		boolean process = false;

		do
        {
           process = false;

           for (final FolderTreeNodeEx ftn : list)
           {
        	   if (data.containsKey(ftn.pid) && !processed.contains(ftn.fid))
        	   {
        		  data.get(ftn.pid).insert(ftn);
        		  process = true;
        		  processed.add(ftn.fid);
        	   }
           }
        } while (process);
		
		deleteEmptyNodes(root);
		sortFullTree(root);
	}
	
	@Override protected FolderTreeNode createFoldersTreeNode(FolderTreeNode parent) { return new FolderTreeNodeEx(this, parent); }
	
	class FolderTreeNodeEx extends FolderTreeNode{
		public String fid = "";
		public String pid = "";
		
		public FolderTreeNodeEx(TreeNodeFactory foldersTree, FolderTreeNode parent) {
			super(foldersTree, parent);
			//nodeIsLeaf = true;
		}
	}
}
