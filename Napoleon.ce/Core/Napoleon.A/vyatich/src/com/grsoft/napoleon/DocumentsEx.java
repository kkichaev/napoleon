package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.StockImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected String orgInfo(Org o) {
		String txt = super.orgInfo(o);
		txt += "<br>баланс:<i>" + Util.IntToScaleStr(((OrgEx)o).balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</i>";
		
		if (orgInStock(o.id))
			txt += getString(R.string.orginstock);
		
		return txt;
	}
	
	private boolean orgInStock(String id) {
		return new StockImpl().read("id", id);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View orgI = findViewById(R.id.btnOrgInfo); 
		orgI.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String test = ((OrgEx)org.getData()).info;
				test = test.replace("\\n", "\n");
				Toast.makeText(DocumentsEx.this, test, Toast.LENGTH_LONG).show();
			}
		});
		
		if( ((OrgEx)org.getData()).info.length() == 0 )
			orgI.setEnabled(false);
	}
}
