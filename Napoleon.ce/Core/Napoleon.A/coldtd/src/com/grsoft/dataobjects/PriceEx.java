package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;

@TableInfo(name="Price", keyFields = "id", indexes="barcode")
public class PriceEx extends PricePrint {
	public String barcode;
	public String packBarcode;
	public String assortimentGroup;
	public int priceOrder;
	/**
	 * Группа в ежедневном отчете
	 */
	public int reportGroup;
	public String articul = "";
	public String packOKEI;
}
