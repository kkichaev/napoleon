package com.grsoft.dataobjects;

import com.grsoft.napoleon.BarcodeData;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SalesItemEx extends SalesItem {
//	@FieldOrder(order=OrderItem.USER_FIELDS)
//	public String ido;

	@FieldOrder(order=OrderItem.USER_FIELDS + 1)
	public String uid = UUID.randomUUID().toString().replace("-", "");

	@FieldOrder(order = USER_FIELDS + 2)
	public List<ScannedItems> barcodes = new ArrayList<>();

	public int factQty() {
		int qty = 0;
		for(ScannedItems si : barcodes)
			qty += si.qty;
		return qty;
	}

	public boolean haveBC(String bc) {
		for(ScannedItems si : barcodes)
			if(si.barcode.equals(bc))
				return true;

		return false;
	}

	public boolean scanned() { return qty == factQty(); }

	public boolean canAdd(Gtin gtin) {
		int rest = qty - factQty();
		return gtin.qty <= rest;
	}
}
