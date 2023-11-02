package com.grsoft.database;

import android.view.View;

import com.grsoft.napoleon.documents.Itemsable;

public class PriceTreeNode extends TreeNode
{
	private String name;
	private long priceRowid;
	private String id;
	
	@Deprecated
	private Itemsable document;
	
	@Deprecated
	public PriceTreeNode(TreeNode parent, long rid, 
			String name, Itemsable document)
	{
		super(parent);
		
		this.priceRowid = rid;
		this.name = name;
		this.document = document;
	}

	public PriceTreeNode(TreeNode parent, long rid, String name, String id)
	{
		super(parent);
		
		this.priceRowid = rid;
		this.name = name;
		this.id = id;
	}

	public String getName() { return name; }
	
	@Override
	@Deprecated
	/***
	 * open()
	 */
	public void onClick(View v) { document.editItem(priceRowid, v.getContext()); }
	
	@Deprecated
	public long getPriceId() { return priceRowid;	}
	
	@Override
	public String toString() { return name;	}

	@Override
	public boolean isLeaf()	{ return false;	}
	
	@Override
	public boolean hasChilds() { return false;	}
	
	@Override
	public int compareTo(TreeNode treeNode) {
		if (this.getClass() != treeNode.getClass())
			return 1;
		else
			return super.compareTo(treeNode);
	}
	
	@Override
	public long getRowid() { return priceRowid; }
	
	public String getId() { return id; }
}
