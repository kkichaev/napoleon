package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ItemsAuditImpl;
import com.grsoft.napoleon.R;

public class ItemsAuditDoc extends DateDocType {
	static ItemsAuditDoc instance;
	
	public ItemsAuditDoc() {
		super("Аудит товаров", "ItemsAudit", ItemsAuditImpl.class);
	}
	
	public static DocType instance() {
		if( instance == null )
			instance = new ItemsAuditDoc();
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.items_audit;
	}
}
