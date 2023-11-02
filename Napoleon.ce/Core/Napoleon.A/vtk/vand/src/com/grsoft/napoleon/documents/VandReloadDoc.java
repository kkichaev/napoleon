package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.VandReloadImpl;
import com.grsoft.napoleon.R;

public class VandReloadDoc extends DateDocType {
	static VandReloadDoc instance;
	
	public static DocType instance() {
		if(instance == null)
			instance = new VandReloadDoc();
		return instance;
	}
	
	VandReloadDoc() {
		super("Перезагрузка", "VandReload", VandReloadImpl.class);
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.reload_doc;
	}	
}
