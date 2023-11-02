package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseAdapter;

public class WarehouseMover implements PriceMover {
	
	WarehouseAdapter adapter = null;
	int curFolderID = -1;
	
	public WarehouseMover() {
	}

	public void setAdapter(WarehouseAdapter adapter) { this.adapter = adapter; }
	
	public int getFolderID() { return curFolderID; }
	
	PriceTreeNode findPriceNode(FolderTreeNode cf, boolean next, String id) {
		PriceTreeNode prevNode = null, nextNode = null;

		cf.loadNodes();
		int i = 0;
		List<TreeNode> childs = cf.getChilds();
		for(; i < childs.size(); i++) {
			TreeNode tn = childs.get(i);
			if(!(tn instanceof PriceTreeNode))
				continue;
			
			PriceTreeNode ptn = (PriceTreeNode)tn; 
			if( ptn.getId().equals(id) ) {
				if(next) {
					for( ++i; i<childs.size(); i++ ) {
						TreeNode cn = childs.get(i);
						if(cn instanceof PriceTreeNode) {
							nextNode = (PriceTreeNode) cn;
							break;
						}
						
					}
				}
				break;
			}
			prevNode = ptn;
		}

		return next ? nextNode : prevNode;
	}
	
	@Override
	public PriceImpl move(PriceImpl price, boolean next) {
		Price p = price.getData();
		
		PriceImpl ret = null;
		
		FolderTreeNode cf = adapter.getRootNode();
		if( !adapter.isExpanded() ) {
			cf = cf.findFolder(p.folderID);
		} 
		if( cf != null ) {
			PriceTreeNode nextNode = findPriceNode(cf, next, p.id);
			if(nextNode == null)
				nextNode = loadNextPriceNode(cf, next);
			if(nextNode != null) {
				ret = new PriceImpl();
				p = ret.getData();
				p.id = nextNode.getId();
				ret.read();
				ret.close();
			}
		}
		return ret;
	}

	private PriceTreeNode loadNextPriceNode(FolderTreeNode cf, boolean next) {
		PriceTreeNode ret = null;
		if(adapter instanceof FoldersAdapter) {
			while(ret == null) {
				int id = ((FoldersAdapter)adapter).getNextFolderID(next, cf.id);
				if(id < 0 || id == cf.id)
					break;
				
				cf = adapter.getRootNode().findFolder(id);
				if(cf == null)
					break;
				if(next) {
					for(TreeNode tn : cf.getChilds()) {
						if(tn instanceof PriceTreeNode) {
							ret = (PriceTreeNode) tn;
							break;
						}
					}
				} else {
					List<TreeNode> childs = cf.getChilds();
					for( int i=childs.size()-1; i >= 0; i--) {
						TreeNode tn = childs.get(i);
						if(tn instanceof PriceTreeNode) {
							ret = (PriceTreeNode) tn;
							break;
						}						
					}
				}
			}
		}
		return ret;
	}

	public void init() {
		curFolderID = -1;
	}

}
