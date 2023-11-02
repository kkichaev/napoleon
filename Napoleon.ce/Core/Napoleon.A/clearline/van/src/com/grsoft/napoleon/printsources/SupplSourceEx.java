package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;


public class SupplSourceEx extends SupplSource{
	public String bankName = "";
	public String ninn = "";
	public String nkpp = "";
	public String bic = "";
	public String k_acc = "";
	public String acc = "";
	
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		
		FirmEx f = (FirmEx) firm;
		bankName = f.bankName;
		
		String[] nn = f.inn.split("/");
		if(nn.length > 0)
			ninn = nn[0];
		
		if(nn.length > 1)
			nkpp = nn[1];
		
		bic = f.bic;
		k_acc = f.k_acc;
		acc = f.acc;
	}
}
