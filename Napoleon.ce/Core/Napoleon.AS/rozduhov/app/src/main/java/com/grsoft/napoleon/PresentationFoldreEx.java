package com.grsoft.napoleon;

public class PresentationFoldreEx extends PresentationFolder {
	@Override
	public void editItem(long rowid) {
		PresentationData data = items.getData(rowid);
		if( data != null ) {
			PricePhotoList.open(this, data.rowid, docRowId);
//			PricePresentation.open(this, path, docRowId, selection);
//			PricePresentationFolder.open(this, rowid, docRowId, selection);
		}
	}
}
