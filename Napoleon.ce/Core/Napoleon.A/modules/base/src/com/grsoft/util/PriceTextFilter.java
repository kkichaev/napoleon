package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;

public class PriceTextFilter extends Filter {
	public static String NAME = "PriceTextFilter"; 
	public static final String SRCH_NAME_FLD = "srchName";
	public static String SRCH_ID_FLD = "id";
	public String srchFieldName = SRCH_NAME_FLD;
	
	public PriceTextFilter() {
		super(NAME);
	}

	protected void collectFolderID(TreeNode node, List<Integer> fids) {
		addFolderId(node, fids);			
		
		for (TreeNode child: node.getChilds()) {
			if (child.hasChilds())
				collectFolderID(child, fids);
			else
				addFolderId(child, fids);
		}
	}
	
	private void addFolderId(TreeNode node, List<Integer> fids) {
		if (node instanceof FolderTreeNode) {
			FolderTreeNode ftn = (FolderTreeNode)node;
			
			if (!fids.contains(ftn.id))
				fids.add(ftn.id);
		}	
	}
	
	public void build(WarehouseAdapter adapter, String cond){
		StringBuilder sbWhere = new StringBuilder();
		
		if (cond.trim().length() > 0){
			ArrayList<Integer> fids = new ArrayList<Integer>();
			makeSearchStr(cond, sbWhere);
			
			if(!adapter.isExpanded()){
				collectFolderID(adapter.getFolderTop(), fids);
			
				if (fids.size() > 0){
					String fidsBeforeStr = fids.toString();
					String fidsStr =  fidsBeforeStr.substring(1, fidsBeforeStr.length()-1);
					String priceTable = DataObjectInfo.getInstance().getTableName(Price.class);
					if( sbWhere.length() > 0 )
						sbWhere.append(" AND ");
					sbWhere.append(priceTable).append(".folderid IN (").append(fidsStr).append(")");
				}
			}
		}	
		
		where = sbWhere.toString();
	}

	protected void makeSearchStr(String cond, StringBuilder sbWhere) {
		cond = cond.replace ("|", "||").replace("_", "|_");
		sbWhere.append("(")
		.append(srchFieldName)
		.append(" LIKE '%").append(cond.toUpperCase()).append("%' ESCAPE '|' )");
	}
}
