package com.grsoft.napoleon.printsources;

public class SalesPrintItemsEx extends SalesPrintItems {

	private static final long serialVersionUID = 1L;

	boolean useTax;
	public SalesPrintItemsEx(SalesPrint owner, boolean useTax) {
		super(owner);
		this.useTax = useTax;
	}

	@Override
	public void calculate() {
		super.calculate();
		if( !useTax || isumtax == 0 ) {
			pagesumtax = "-";
			sumtax = "-";
		}
	}
}
