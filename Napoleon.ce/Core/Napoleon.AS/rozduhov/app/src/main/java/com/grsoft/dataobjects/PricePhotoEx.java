package com.grsoft.dataobjects;

import com.grsoft.database.BlobSource;

public class PricePhotoEx extends DataObject {
	public String id = "";

	/***
	 * Фотография
	 */
	@BlobSource
	public byte[] photo;
}
