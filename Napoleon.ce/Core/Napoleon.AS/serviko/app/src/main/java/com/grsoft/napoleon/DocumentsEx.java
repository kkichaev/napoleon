package com.grsoft.napoleon;

import android.view.Menu;
import android.view.MenuItem;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.ClientCardDoc;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class DocumentsEx extends Documents {
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		
		String info = ((OrgEx)o).getInfo();
		if(info.length() > 0)
			ret += "<br>" + info;
		
		return ret; 
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == ClientCardDoc.instance()) {
			ClientCardReport.open(this, org.getData().id);
			finish();
		}else if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		}else
			super.adjustViewForDocType(docType);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		if(((OrgEx)org.getData()).regionID.trim().length() > 0 && allowKupec()){
			MenuItem item = menu.add(Menu.NONE, R.id.action_item, Menu.NONE, R.string.kupec);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			item.setIcon(getResources().getDrawable(R.drawable.ic_action));
		}

		return true;
	}

	private boolean allowKupec() {
		ConfigImpl config = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		config.getValue(sb, "Купец");

		return sb.toString().equals("1");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_item){
			KupecView.open(this, org.getData().id);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
