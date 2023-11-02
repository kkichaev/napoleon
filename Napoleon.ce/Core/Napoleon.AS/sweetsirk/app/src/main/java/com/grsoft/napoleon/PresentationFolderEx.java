package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.Itemsable;

public class PresentationFolderEx extends PresentationFolder {
	@Override
	public void editItem(long rowid) {
		((Itemsable)doc).editItem(rowid, this);
	}
	
	@Override
	protected void adapterInit() {
		super.adapterInit();
		
		if (doc != null && doc.getId().length() > 0) {
			adapter.putFilter(new WarehouseEx.OrgMaskFilter(doc.getId()));
		}
	}
}
