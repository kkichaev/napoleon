package com.grsoft.napoleon;

import android.widget.Toast;
import com.grsoft.napoleon.documents.DebtDoc;

public class IncassEditEx extends IncassEdit {
	@Override
	protected void onPause() {
		super.onPause();
		try{
			DebtDoc.instance().refreshDocSum(doc.getId());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(doc.isEditable()){
			int maxdocsum = PrgCfgHelper.getMaxDocSum();
			
			if(maxdocsum == 0 || maxdocsum > getSum()){
				save();
				super.onBackPressed();
			}else
				Toast.makeText(this, R.string.sum_doc_exceed, Toast.LENGTH_SHORT).show();
		}else		
			super.onBackPressed();
	}
	
	@Override
	protected void send() {
		int maxdocsum = PrgCfgHelper.getMaxDocSum();
		
		if(maxdocsum == 0 || maxdocsum > getSum())
			super.send();
		else
			Toast.makeText(this, R.string.sum_doc_exceed, Toast.LENGTH_SHORT).show();	
	}
}
