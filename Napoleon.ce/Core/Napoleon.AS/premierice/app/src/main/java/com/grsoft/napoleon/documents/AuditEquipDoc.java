package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.InvEquImpl;
import com.grsoft.napoleon.R;

public class AuditEquipDoc extends DateDocType {
	public static final String OBJ_NAME = "AuditEquip";
	
	private static AuditEquipDoc instance;
	
	protected AuditEquipDoc() {
		super(OBJ_NAME, OBJ_NAME, InvEquImpl.class);
	}
	
	public static AuditEquipDoc instance(){
		if (instance == null)
			instance = new AuditEquipDoc();
		
		return instance;
	}
	
	@Override public boolean outOfScript() { return true; }
	
	@Override public int getDocTitle() { return R.string.invequdoc_title;}

	@Override public int getResurce2Id() { return R.drawable.inv_equ_2; }
	@Override public int getResurceId() { return R.drawable.inv_equ; }

}
