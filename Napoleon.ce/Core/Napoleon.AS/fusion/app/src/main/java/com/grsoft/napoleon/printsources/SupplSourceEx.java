package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Firm;

public class SupplSourceEx extends SupplSource {
	@PrintInfo(name="ФактАдрес")
	public String supl_factAddress;

	@PrintInfo(name="ПолнНаим")
	public String supl_fullAddress;
	
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		
		supl_factAddress = firm.factAddress.length() > 0 ? firm.factAddress : firm.address;
		supl_fullAddress = firm.fullName;
	}
}
