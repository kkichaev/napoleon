package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.Map;
import android.database.sqlite.SQLiteDatabase;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.dataobjects.ContractDefItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.ContractDefImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;


public class ContractAdapter extends FoldersAdapter {
	private ContractDefImpl data = new ContractDefImpl();
	private PriceImpl price = new PriceImpl();
	
	public ContractAdapter(WarehouseNew warehouse, String defid) {
		super(warehouse);
		data.read("id", defid);
	}

	
	@Override
	protected void fillTree(SQLiteDatabase database) {
		root.getChilds().clear();
		Map<String, FolderTreeNode> groups = new HashMap<String, FolderTreeNode>();
		
		for(ContractDefItem  i : data.getData().items){
			if (price.read("id", i.id)){
				PriceEx pe = (PriceEx) price.getData();
				if(!groups.containsKey(pe.group)){
					FolderTreeNode node = createFoldersTreeNode(root);
					node.id = 0;
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
