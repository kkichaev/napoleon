package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.ContractDef;
import com.grsoft.dataobjects.ContractDefItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.WarehouseManager;


@Deprecated
public class ReturnAdapter extends FoldersAdapter {
	List<ContractDef> data = new ArrayList<ContractDef>();
	HashSet<String> priceFilter = new HashSet<String>();
	
	public ReturnAdapter(WarehouseManager warehouse, String orgId) {
		super(warehouse);
		
		final long time = new Date().getTime();
		final OrgMatrixImpl om = new OrgMatrixImpl();
		final MatrixImpl mi = new MatrixImpl();
		final Matrix matrix = mi.getData();
		final OrgMatrix orgm = om.getData(); 

		orgm.id = orgId;
		
		DataTraveler.travel(ContractDef.class, new DataTraveler.Travel<ContractDef>() {
			@Override
			public boolean travel(DataTraveler<ContractDef> item) {
				data.add(item.data);
				orgm.cdef = item.data.id;
				boolean readed = false;
				if( om.read() ) {
					matrix.name = orgm.name;
					if(mi.read()) {
						readed = true;
						for(MatrixItem mtxi : matrix.items)
							priceFilter.add(mtxi.id);
					}
				} 
				if(!readed) {
					for(ContractDefItem cdi : item.data.items)
						priceFilter.add(cdi.id);
				}
				
				item.data = new ContractDef();
				return true;
		}}, String.format("start<=%d and finish>=%d", time, time));
		
		om.close();
		mi.close();
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
		return new ReturnNode(this, parent, priceFilter);
	}
}

class ReturnNode extends FolderTreeNode{
	public ContractDef contract;
	HashSet<String> priceFilter;
	
	public ReturnNode(TreeNodeFactory foldersTree, FolderTreeNode parent, HashSet<String> priceFilter) {
		super(foldersTree, parent);
		this.priceFilter = priceFilter;
	}
	
	@Override
	public void open() {
		
		
		if (childs == null){ 
			childs = new ArrayList<TreeNode>();
		
			PriceImpl pimpl = new PriceImpl();
			PriceEx pi = (PriceEx) pimpl.getData();
			for(ContractDefItem i : contract.items){
				if(priceFilter.contains(i.id) == false)
					continue;
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
