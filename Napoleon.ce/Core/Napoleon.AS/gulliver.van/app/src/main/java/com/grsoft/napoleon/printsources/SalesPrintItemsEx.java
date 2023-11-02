package com.grsoft.napoleon.printsources;


public class SalesPrintItemsEx extends SalesPrintItems {
//	public int iqtypack = 0;
//	public int ipageqtypack = 0;
//	public String qtypack = "";
//	public String pageqtypack = "";
	boolean useTax;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SalesPrintItemsEx(SalesPrint owner, boolean useTax) {
		super(owner);
		this.useTax = useTax;
	}
	
	@Override
	public void calculate() {
		super.calculate();
//		SalesItemPrintEx sip = (SalesItemPrintEx) get(index);
//		iqtypack += sip.iqtypack;
//		ipageqtypack += sip.iqtypack;
//		
//		pageqtypack = Util.IntToScaleStr(ipageqtypack, Consts.QTY_SCALE);
//		qtypack = Util.IntToScaleStr(iqtypack, Consts.QTY_SCALE);
		
		if( !useTax ) {
			pagesumtax = "";
			sumtax = "";
		}

	}

}
