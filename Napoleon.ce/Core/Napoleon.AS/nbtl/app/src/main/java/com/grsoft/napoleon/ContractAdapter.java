package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.dataobjects.ContractDefItem;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ContractDefImpl;
import com.grsoft.dataobjects.impl.MatrixImpl;
import com.grsoft.dataobjects.impl.OrgMatrixImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;


public class ContractAdapter extends FoldersAdapter {
	private ContractDefImpl data = new ContractDefImpl();
	private PriceImpl price = new PriceImpl();
	HashSet<String> priceFilter = new HashSet<String>();
	
	public ContractAdapter(Warehouse warehouse, String defid, String orgId) {
		super(warehouse);
		data.read("id", defid);
	
		final OrgMatrixImpl om = new OrgMatrixImpl();
		final MatrixImpl mi = new MatrixImpl();
		final Matrix matrix = mi.getData();
		final OrgMatrix orgm = om.getData(); 
	
		orgm.id = orgId;
		orgm.cdef = defid;
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
			for(ContractDefItem cdi : data.getData().items)
				priceFilter.add(cdi.id);
		}
	}

	@Override
	public String getWhereStr() {
		String ret = super.getWhereStr();
		if(ret.length() > 0)
			ret += " AND ";
		ret += "isGoods=0";
		return ret;
	}
	
	
	@Override
	protected void fillTree(SQLiteDatabase database) {
		root.getChilds().clear();
		Map<String, FolderTreeNode> groups = new HashMap<String, FolderTreeNode>();
		
		int id = 0;
		for(ContractDefItem  i : data.getData().items){
			if(priceFilter.contains(i.id) == false)
				continue;
			if (price.read("id", i.id)){
				PriceEx pe = (PriceEx) price.getData();
				if(!groups.containsKey(pe.group)){
					FolderTreeNode node = createFoldersTreeNode(root);
					node.id = id++;
					node.level = 0;
					node.name = pe.group;
					node.setLeaf(true);
					node.getChilds().add(createPriceTreeNode(node, price.getRowid(), pe.name, pe.id));
					node.priceLoaded = true;
					root.insert(node);
					groups.put(pe.group, node);
				}else{
					FolderTreeNode node = groups.get(pe.group);
					node.getChilds().add(createPriceTreeNode(node, price.getRowid(), pe.name, pe.id));
				}
			}
		}
	}
}
