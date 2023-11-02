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
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	
	@Override
	protected String getItemName(Price p) {
		return super.getItemName(p) + "\n" + ((PriceEx) p).barcode;
	}

	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		
		DocType dt = DocType.getCurDoc(); 
		if ((dt != OrderDoc.instance() && dt != ReturnDoc.instance() && dt != SalesDoc.instance()) 
				|| docRowId == ExtrasConst.INVALID_ID) {
			return super.createListAdapter();
		}
		
		if (document.getRowid() == ExtrasConst.INVALID_ID)
			document.read(docRowId);
		OrgImpl orgImpl = new OrgImpl();
		Org oe = orgImpl.getData();
		oe.id = document.getId();

		Object ddata = document.getData();
		String iddog = null;
		
		if(ddata instanceof OrderEx)
			iddog =	((OrderEx)ddata).iddog;
		else if (ddata instanceof ReturnEx)
			iddog =	((ReturnEx)ddata).iddog;
		else if (ddata instanceof SalesEx)
			iddog = ((SalesEx)ddata).iddog;
		
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
	
	@Override
	protected Filter createZeroPositionFilter() {
		return new ZeroFilter();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		boolean useWhereStr;
		
		public ZeroFilter() {
			Object ddata = document.getData();
			if(ddata instanceof OrderEx) {
				useWhereStr = (((OrderEx)ddata).skladIndex == 0);
			} else
				useWhereStr = true;			
		}
		
		@Override
		public String getWhereStr() {
			return useWhereStr ?  super.getWhereStr() : "";
		}
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( useWhereStr )
				return true;
			price.read(priceRowID);
			
			return (((OrderImpl)document).getItemValue(price.getData()) > 0);
		}
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
