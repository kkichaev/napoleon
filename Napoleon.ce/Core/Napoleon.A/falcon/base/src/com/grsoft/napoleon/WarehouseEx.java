package com.grsoft.napoleon;

import android.view.Menu;
import android.widget.BaseAdapter;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;

public class WarehouseEx extends WarehouseNew {
	
	static long lastRowID = 0;
	
	@Override
	protected String getItemName(Price p) {
		return super.getItemName(p) + "\n" + ((PriceEx) p).barcode;
	}

	@Override
	protected BaseAdapter createListAdapter() {
		if(lastRowID != docRowId)
			FoldersAdapter.resetCache();
		
		lastRowID = docRowId;
		
		DocType dt = DocType.getCurDoc(); 
		if ((dt != OrderDoc.instance() && dt != ReturnDoc.instance()) || docRowId == ExtrasConst.INVALID_ID) {
			return super.createListAdapter();
		}
		
		if (document.getRowid() == ExtrasConst.INVALID_ID)
			document.read(docRowId);
		OrgImpl orgImpl = new OrgImpl();
		Org oe = orgImpl.getData();
		oe.id = document.getId();

		Object ddata = document.getData();
		String iddog = (ddata instanceof OrderEx) ? 
				((OrderEx)ddata).iddog : (ddata instanceof ReturnEx) ?
				((ReturnEx)ddata).iddog :
				null;
		OrgDogovor dgv = null;		
		if( orgImpl.read() && iddog != null ) {
			dgv = DocHelper.getDogovor((OrgEx)oe, iddog);
		}
		orgImpl.close();
		
		return new FoldersAdapterEx(this, dgv);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 boolean result = super.onCreateOptionsMenu(menu);
		 menu.findItem(R.id.itMatrix).setVisible(false);
		 return result;
	}
}

class FoldersAdapterEx extends FoldersAdapter {

	String type;
	
	public FoldersAdapterEx(WarehouseManager warehouse, OrgDogovor dogovor) {
		super(warehouse);
		if( dogovor != null )
			type = dogovor.type;
	}
	
	@Override
	public String getWhereStr() {
		String res = super.getWhereStr();
		if( type != null && type.length() > 0 ) {
			if( res.length() > 0 )
				res += " AND ";
			res += "folderid in (select id from folder where type='" + type + "')";
		}
		return res;
	}
}
