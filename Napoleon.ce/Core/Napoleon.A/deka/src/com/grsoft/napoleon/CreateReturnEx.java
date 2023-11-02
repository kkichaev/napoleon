package com.grsoft.napoleon;

import com.grsoft.dataobjects.Distributor;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	@Override int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
	protected void initView() {
		final ReturnEx r = (ReturnEx) doc.getData();
		
		Spinner spDistr = (Spinner)findViewById(R.id.spDistr);
		DialogHelper.loadSpinnerFromDataObject(spDistr, Distributor.class, new DialogHelper.Selected<Distributor>() {
			@Override public boolean isSelected(Distributor object) { return r.distr.equals(object.id); }
		}, false, "name");
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);

		Distributor sd = (Distributor) ((Spinner) findViewById(R.id.spDistr)).getSelectedItem();
		if( sd != null)
			((ReturnEx)r).distr = sd.id;
	}
}
