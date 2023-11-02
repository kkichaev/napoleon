package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import android.os.Bundle;

public class CreateSalesEx extends CreateSales {
	private ArrayList<CharSequence> firms = new ArrayList<CharSequence>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConfigImpl config = new ConfigImpl();
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, salesImpl.getData().supplyer);
	}
	
	@Override protected void setFirmSelection() {}
	@Override protected void closeFirmAdapter() {}
	
	@Override
	protected void saveFirm() {
		salesImpl.getData().supplyer = spFirma.getSelectedItemPosition();
	}
}
