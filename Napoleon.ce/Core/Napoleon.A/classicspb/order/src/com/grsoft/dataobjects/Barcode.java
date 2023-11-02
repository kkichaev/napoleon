package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="barcode", keyFields="created")
public class Barcode extends CreateDocDataObject {
	public List<BarcodeItem> items = new ArrayList<BarcodeItem>();
	public Date visitDoc = new Date(1000);
}
