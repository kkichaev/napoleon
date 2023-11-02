package com.grsoft.napoleon;

public class PricePresentationFolderEx extends PricePresentationFolder {
	@Override
	protected void initPresentList() {
		list = new PresentationList();
		list.setWhereStr(String.format("price.rowid=%d",priceId));
		list.fill(false);

//		list = PresentationFolderW.items;
//		PresentationFolderW.items.setWhereStr(String.format("price.rowid=%d",priceId));
//		PresentationFolderW.items.fill(false);
	}
}
