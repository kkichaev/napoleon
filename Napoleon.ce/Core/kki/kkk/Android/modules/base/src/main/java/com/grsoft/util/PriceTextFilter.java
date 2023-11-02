package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.napoleon.Features;

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
	
	public void build(WarehouseAdapter adapter, String cond, boolean searchExcact){
		StringBuilder sbWhere = new StringBuilder();
		
		if (cond.trim().length() > 0){
			ArrayList<Integer> fids = new ArrayList<Integer>();
			makeSearchStr(cond, sbWhere, searchExcact);
			
			if(!adapter.isExpanded()){
				collectFolderID(adapter.getFolderTop(), fids);
			
				if (fids.size() > 0){
					String fidsBeforeStr = fids.toString();
					String fidsStr =  fidsBeforeStr.substring(1, fidsBeforeStr.length()-1);
					if( sbWhere.length() > 0 )
						sbWhere.append(" AND ");
					sbWhere.append("price.folderid IN (").append(fidsStr).append(")");
				}
			}
		}	
		
		where = sbWhere.toString();
	}

	protected void makeSearchStr(String cond, StringBuilder sbWhere, boolean exact) {
		cond = cond.replace ("|", "||").replace("_", "|_");

		if(exact) {
			sbWhere.append("(")
					.append(srchFieldName)
					.append(" = '").append(cond).append("')");
		} else {
			if(Features.MULTI_WORD_SEARCH) {
				String[] parts = cond.split("\\s+");
				sbWhere.append("(");
				boolean starting = false;
				for(String p : parts) {
					if(starting) {
						sbWhere.append(" AND ");
					} else  {
						starting = true;
					}
					sbWhere.append(srchFieldName).append(" LIKE '%").append(p.toUpperCase()).append("%' ");
				}
				sbWhere.append(" ESCAPE '|' )");
			} else {
				sbWhere.append("(")
						.append(srchFieldName)
						.append(" LIKE '%").append(cond.toUpperCase()).append("%' ESCAPE '|' )");
			}
		}
	}
}
