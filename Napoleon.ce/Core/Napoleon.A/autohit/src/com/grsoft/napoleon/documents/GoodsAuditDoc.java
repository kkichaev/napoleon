package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.GoodsAuditImpl;
import com.grsoft.napoleon.R;

public class GoodsAuditDoc extends DateDocType {
	static GoodsAuditDoc instance = null;
	static public GoodsAuditDoc instance() {
		if(instance == null)
			instance = new GoodsAuditDoc();
		return instance;
	}
	
	GoodsAuditDoc() {
		super("Аудит товаров", "GoodsAudit", GoodsAuditImpl.class);
	}

	@Override public int getResurceId() { return R.drawable.goods_audit_doc; }
}
