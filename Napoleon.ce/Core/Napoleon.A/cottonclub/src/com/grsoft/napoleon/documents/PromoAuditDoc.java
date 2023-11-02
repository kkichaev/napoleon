package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.PromoAuditImpl;
import com.grsoft.napoleon.R;

public class PromoAuditDoc extends DateDocType {
	static PromoAuditDoc instance = null;
	
	public PromoAuditDoc() {
		super("Аудит акций", "PromoAudit", PromoAuditImpl.class);
	}
	
	public static DocType instance() {
		if(instance == null)
			instance = new PromoAuditDoc();
		
		return instance;
	}
	
	@Override public int getDocTytle() { return R.string.promo_audit; }
	@Override
	public int getResurceId() {	return R.drawable.promo; }
}
