package com.grsoft.ads.dataobjects;

import com.grsoft.database.BlobSource;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class OrderPhotoItem extends DataObject {
	/**
	 * Снимок с камеры
	 */
	@BlobSource
	@FieldOrder(order=0)
	public byte[] id;
}
